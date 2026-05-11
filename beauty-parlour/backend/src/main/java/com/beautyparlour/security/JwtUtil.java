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
            @Value("${app.jwt.secret:beauty-parlour-secret-key-must-be-at-least-256-bits-long-for-hs256}") final String secret,
            @Value("${app.jwt.access-token-expiry-ms:3600000}") final long accessTokenExpiry,
            @Value("${app.jwt.refresh-token-expiry-ms:604800000}") final long refreshTokenExpiry
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateAccessToken(final String email, final String role) {
        return buildToken(email, role, accessTokenExpiry, "access");
    }

    public String generateRefreshToken(final String email, final String role) {
        return buildToken(email, role, refreshTokenExpiry, "refresh");
    }

    private String buildToken(final String subject, final String role,
                               final long expiryMs, final String type) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(final String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    public String extractEmail(final String token) {
        return parseToken(token).getSubject();
    }

    public boolean isTokenValid(final String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
