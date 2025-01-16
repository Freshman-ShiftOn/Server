package com.epicode.apigatewayservice.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class InviteJwtAuthenticationFilter extends AbstractGatewayFilterFactory<InviteJwtAuthenticationFilter.Config> {
    private final SecretKey secretKey;

    public InviteJwtAuthenticationFilter(@Value("${jwt.secret-key}") String secretKey) {
        super(Config.class);
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("X-Invite-Token")) {//front에서 헤더 설정할 키
                return onError(exchange, "Invite Token 헤더가 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            String inviteToken = request.getHeaders().getFirst("X-Invite-Token");

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(inviteToken)
                        .getBody();

                String inviterEmail = claims.get("inviterEmail", String.class);
                String email = claims.get("email", String.class);
                Long branchId = claims.get("branchId", Long.class);

                if (inviterEmail == null || inviterEmail.isEmpty()) {
                    return onError(exchange, "초대장 토큰에 초대자의 email이 없습니다.", HttpStatus.UNAUTHORIZED);
                }
                if (email == null || email.isEmpty()) {
                    return onError(exchange, "초대장 토큰에 초대받을 이의 email이 없습니다.", HttpStatus.UNAUTHORIZED);
                }
                if (branchId == null) {
                    return onError(exchange, "초대장 토큰에 branchId가 없습니다.", HttpStatus.UNAUTHORIZED);
                }

                // 기존 요청에 초대장 데이터를 추가
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Inviter-Email", email)
                        .header("X-Invitee-Email", email)
                        .header("X-Invite-Branch-Id", String.valueOf(branchId))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (ExpiredJwtException e) {
                return onError(exchange, "초대장 토큰이 만료되었습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (JwtException e) {
                return onError(exchange, "초대장 토큰 검증 실패: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorMessage = String.format("{\"status\": %d, \"error\": \"%s\"}", httpStatus.value(), error);
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer))
                .then(Mono.defer(() -> {
                    response.setComplete();
                    return Mono.empty();
                }));
    }
}
