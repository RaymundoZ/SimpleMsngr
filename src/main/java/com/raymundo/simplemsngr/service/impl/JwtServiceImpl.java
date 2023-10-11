package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String SECRET_KEY = "944641253C8054206AA5D54603C969E44F995AADEEC04ADEEB1EA1713A05B817FB6C0D4F5C4F760756F26EB42762D74E99637FF4D8A2F727A1FE3711030457E6";

    @Override
    public String generateToken(String username, String password) {
        return generateToken(Map.of("username", username, "password", password));
    }

    @Override
    public String generateToken(String email) {
        return generateToken(Map.of("email", email));
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
                .unauthenticated(getUsername(token), getPassword(token));
        WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();
        authToken.setDetails(detailsSource.buildDetails(request));
        return authToken;
    }

    @Override
    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    private String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    private String getPassword(String token) {
        return getClaims(token).get("password", String.class);
    }

    private String generateToken(Map<String, String> claims) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 3_600_000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
