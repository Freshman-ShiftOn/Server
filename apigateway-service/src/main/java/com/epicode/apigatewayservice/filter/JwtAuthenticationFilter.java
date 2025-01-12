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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private final SecretKey secretKey;

    public JwtAuthenticationFilter(@Value("${jwt.secret-key}") String secretKey) {
        super(Config.class);
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    public static class Config {
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("Authorization 헤더가 없습니다.");
                return onError(exchange, "Authorization 헤더가 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return onError(exchange, "Authorization 헤더 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            String jwt = authorizationHeader.replace("Bearer ", "");

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = claims.getSubject();
                List<Integer> branchIdsList = claims.get("branchIds", List.class); // List<Integer>로 받음
                Long userId = claims.get("userId",Long.class);

                if(userId<0 || userId==0){
                    return onError(exchange, "JWT 토큰에 userId가 없습니다.", HttpStatus.UNAUTHORIZED);
                }
                if (email == null || email.isEmpty()) {
                    return onError(exchange, "JWT 토큰에 email이 없습니다.", HttpStatus.UNAUTHORIZED);
                }
                if (branchIdsList == null || branchIdsList.isEmpty()) {
                    return onError(exchange, "JWT 토큰에 branchIds가 없습니다.", HttpStatus.UNAUTHORIZED);
                }

                // 기존 요청에 사용자 정보를 추가하여 새로운 요청 생성
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Authenticated-User-Id", String.valueOf(userId))
                        .header("X-Authenticated-User", email)
                        .header("X-Branch-Ids", branchIdsList.stream()
                                .map(String::valueOf) // Integer를 String으로 변환
                                .collect(Collectors.joining(","))) // 쉼표로 구분된 String 생성
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (ExpiredJwtException e) {
                return onError(exchange, "JWT 토큰이 만료되었습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (UnsupportedJwtException e) {
                return onError(exchange, "지원되지 않는 JWT 토큰입니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (MalformedJwtException e) {
                return onError(exchange, "잘못된 형식의 JWT 토큰입니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                // 예외 메시지를 로그와 응답에 포함
                e.printStackTrace(); // 자세한 예외 정보를 로그로 출력
                return onError(exchange, "JWT 처리 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        // 로그 기록
        System.out.println("인증 오류: " + error);

        // 응답 설정
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // JSON 에러 메시지 생성
        String errorMessage = String.format("{\"error\": \"%s\"}", error);
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        // 기존 응답이 종료되지 않도록 응답을 확실히 플러시
        return response.writeWith(Mono.just(buffer))
                .then(Mono.defer(() -> {
                    response.setComplete();
                    return Mono.empty();
                }));
    }
}