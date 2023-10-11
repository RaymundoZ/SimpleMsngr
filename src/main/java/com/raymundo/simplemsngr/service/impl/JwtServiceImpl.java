package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.exception.InvalidTokenException;
import com.raymundo.simplemsngr.repository.JwtTokenRepository;
import com.raymundo.simplemsngr.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String SECRET_KEY = "944641253C8054206AA5D54603C969E44F995AADEEC04ADEEB1EA1713A05B817FB6C0D4F5C4F760756F26EB42762D74E99637FF4D8A2F727A1FE3711030457E6";

    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public String generateToken(JwtTokenEntity jwtToken) {
        UUID tokenId = jwtTokenRepository.save(jwtToken).getId();

        Map<String, String> claims = Map.of("id", tokenId.toString());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(jwtToken.getExpiration().toInstant(ZoneOffset.UTC)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public void invalidateToken(String token) throws InvalidTokenException {
        JwtTokenEntity jwtToken = parseToken(token);
        jwtToken.setIsValid(false);
        jwtTokenRepository.save(jwtToken);
    }

    @Override
    public boolean isTokenValid(String token) throws InvalidTokenException {
        JwtTokenEntity jwtToken = parseToken(token);
        return jwtToken.getIsValid();
    }

    @Override
    public JwtTokenEntity parseToken(String token) throws InvalidTokenException {
        return jwtTokenRepository.findById(getId(token))
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private UUID getId(String token) {
        return UUID.fromString(getClaims(token).get("id", String.class));
    }

    private String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    private String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    private String getPassword(String token) {
        return getClaims(token).get("password", String.class);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
