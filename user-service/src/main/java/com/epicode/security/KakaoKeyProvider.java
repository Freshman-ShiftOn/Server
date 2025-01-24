package com.epicode.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

@Component
public class KakaoKeyProvider implements RSAKeyProvider {

    private static final String JWKS_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    private final JwkProvider provider;

    public KakaoKeyProvider() {
        this.provider = new JwkProviderBuilder(JWKS_URL)
                .cached(10, 7, TimeUnit.DAYS) // 최대 10개의 키를 7일간 캐싱
                .build();
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        try {
            // Key ID로 JWK 검색
            Jwk jwk = provider.get(keyId);
            return (RSAPublicKey) jwk.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch public key for Key ID: " + keyId, e);
        }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return null; // 카카오는 개인 키를 제공하지 않음
    }

    @Override
    public String getPrivateKeyId() {
        return ""; // 개인 키가 없으므로 빈 문자열 반환
    }
}
