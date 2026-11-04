package com.esporteclubepelotas.api.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret:super-secret-jwt-key-super-secret-jwt-key-32chars}")
    private String jwtSecret;

    @Value("${security.jwt.expires-in:1h}")
    private String jwtExpiresIn;

    private SecretKey key;
    private long jwtExpirationInMs;

    @PostConstruct
    public void init() {
        // Garante no mínimo 256 bits (32 bytes) para HMAC-SHA256
        String paddedSecret = jwtSecret;
        while (paddedSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            paddedSecret += "-esporte-clube-pelotas-1908";
        }
        this.key = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = parseDurationToMillis(jwtExpiresIn);
    }

    private long parseDurationToMillis(String duration) {
        if (duration == null || duration.isBlank()) return 3600000L; // 1h default
        String trimmed = duration.trim().toLowerCase();
        try {
            if (trimmed.endsWith("h")) {
                long hours = Long.parseLong(trimmed.replace("h", ""));
                return hours * 3600000L;
            } else if (trimmed.endsWith("d")) {
                long days = Long.parseLong(trimmed.replace("d", ""));
                return days * 86400000L;
            } else if (trimmed.endsWith("m")) {
                long minutes = Long.parseLong(trimmed.replace("m", ""));
                return minutes * 60000L;
            } else if (trimmed.endsWith("s")) {
                long seconds = Long.parseLong(trimmed.replace("s", ""));
                return seconds * 1000L;
            }
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            return 3600000L;
        }
    }

    public String generateAccessToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userPrincipal.getId())
                .claim("email", userPrincipal.getEmail())
                .claim("name", userPrincipal.getName())
                .claim("cpf", userPrincipal.getCpf())
                .claim("role", userPrincipal.getRole())
                .claim("permissions", userPrincipal.getPermissions())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (7L * 24 * 3600000L)); // 7 dias

        return Jwts.builder()
                .subject(userPrincipal.getId())
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}

