package com.example.demo.config.jwt;

import com.example.demo.util.EnvUtil;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = EnvUtil.get("JWT_SECRET_KEY");
    private final String JWT_PREFIX = "Bearer ";

    private final long ACCESS_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 30 * 24 * 60 * 60 * 1000; // 30 days

    // SecretKey 생성
    private Key getSigningKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    private String generateToken(String email, long expirationTime) {
        LocalDateTime expiration = LocalDateTime.now().plus(expirationTime, ChronoUnit.MILLIS); // 밀리초를 더하는 방법
        Date expirationDate = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .signWith(getSigningKey()) // SecretKey로 서명
                .compact();
    }

    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    public String getUserEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith(JWT_PREFIX)) {
            return bearer.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .after(new Date());
    }
}
