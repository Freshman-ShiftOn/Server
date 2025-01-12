package com.epicode.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key SECRET_KEY;
    private static final long EXPIRATION_TIME = 86400000; // 24 hours


    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String email,Long[] branchIds,Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("branchIds", branchIds)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
//    public String getEmailFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(SECRET_KEY)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }

//    public Long getUserIdFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(SECRET_KEY)
//                .build()
//                .parseClaimsJws(token)
//                .getBody(); // Claims에서 userId 추출
//        return claims.get("userId", Long.class); // userId를 Long 타입으로 반환
//    }
//
//    public Long[] getBranchIdsFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(SECRET_KEY)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get("branchIds", Long[].class);
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}