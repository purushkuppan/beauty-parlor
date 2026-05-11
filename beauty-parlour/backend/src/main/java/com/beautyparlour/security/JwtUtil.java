package com.beautyparlour.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtUtil(
            @Value("${app.jwt.secret:beauty-parlour-secret-key-must-be-at-least-256-bits-long-for-hs256}") String secret,
            @Value("${app.jwt.access-token-expiry-ms:3600000}") long accessTokenExpiry,
            @Value("${app.jwt.refresh-token-expiry-ms:604800000}") long refreshTokenExpiry
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateAccessToken(String email, String role) {
        return buildToken(email, role, accessTokenExpiry, "access");
    }

    public String generateRefreshToken(String email, String role) {
        return buildToken(email, role, refreshTokenExpiry, "refresh");
    }

    private String buildToken(String subject, String role, long expiryMs, String type) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    public String extractEmail(String token) {
        return parseToken(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
