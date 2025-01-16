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
import java.util.List;

@Component
public class InviteToken {
    private final Key SECRET_KEY;
    private static final long INVITE_EXPIRATION_TIME = 259200000; // 3 days for invite tokens


    public InviteToken(@Value("${jwt.secret-key}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
    }
    // 초대장용 JWT 생성
    public String generateInviteToken(String inviterEmail,String inviteEmail, Long branchId) {
        return Jwts.builder()
                .setSubject("InviteToken")
                .claim("inviterEmail", inviterEmail)
                .claim("email", inviteEmail)
                .claim("branchId", branchId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + INVITE_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    // 초대장 JWT 해석
    public Claims parseInviteToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}