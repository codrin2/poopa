package com.ssafy.foofa.identity.application;

import com.ssafy.foofa.core.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenManager {
    public static final String TOKEN_ISSUER = "FOOFA";

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String memberId, long expirationTime) {
        Claims claims = Jwts.claims().subject(memberId).build();
        String jti = UUID.randomUUID().toString().substring(0, 16) + memberId;
        Date now = new Date();

        return Jwts.builder()
                .id(jti)
                .issuer(TOKEN_ISSUER)
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public TokenInfo parseClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .requireIssuer(TOKEN_ISSUER)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new TokenInfo(claims.getId(), claims.getSubject(), claims.getExpiration().toInstant());
        } catch (ExpiredJwtException ex) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_EXPIRED.getMessage());
        } catch (JwtException ex) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_INVALID.getMessage());
        }
    }

    public TokenInfo parseClaimsFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .requireIssuer(TOKEN_ISSUER)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new TokenInfo(claims.getId(), claims.getSubject(), claims.getExpiration().toInstant());
        } catch (ExpiredJwtException ex) {
            throw new IllegalArgumentException(ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
        } catch (JwtException ex) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_INVALID.getMessage());
        }
    }
}
