package com.epicode.userservice.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String secretKey;

    public JwtAuthenticationFilter(String secretKey) {
        this.secretKey = secretKey;//외부주입
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // JWT 헤더가 없을 경우 요청 통과
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            String email = claims.getSubject();

            // 사용자 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, null);

            // SecurityContext에 인증 객체 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Authenticated user: {}", email);

        } catch (ExpiredJwtException e) {
            log.error("JWT token has expired", e);
            respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
            return;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT token", e);
            respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        } catch (Exception e) {
            log.error("Unexpected error during JWT processing", e);
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
            return;
        }

        chain.doFilter(request, response);
    }

    private void respondWithError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}