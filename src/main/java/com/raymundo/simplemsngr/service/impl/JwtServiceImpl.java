package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.repository.JwtTokenRepository;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.util.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of {@link JwtService}.
 * Service that is responsible for operations with jwt tokens.
 *
 * @author RaymundoZ
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value(value = "${jwt.secret-key}")
    private String SECRET_KEY;

    private final JwtTokenRepository jwtTokenRepository;

    /**
     * Generates jwt token based on provided {@link JwtTokenEntity}.
     *
     * @param jwtToken {@link JwtTokenEntity}
     * @return {@link String} jwt token
     */
    @Override
    public String generateToken(JwtTokenEntity jwtToken) {
        UUID tokenId = jwtTokenRepository.save(jwtToken).getId();

        Map<String, String> claims = Map.of("id", tokenId.toString());
        LocalDateTime localDateTime = jwtToken.getExpiration();
        Date expiration = localDateTime == null ? null : Date.from(localDateTime.toInstant(ZoneOffset.UTC));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Removes jwt token from database.
     * Makes impossible to use this token again.
     *
     * @param token {@link String} token to remove
     * @throws InvalidTokenException exception during token handling
     */
    @Override
    public void invalidateToken(String token) throws InvalidTokenException {
        JwtTokenEntity jwtToken = parseToken(token);
        jwtTokenRepository.delete(jwtToken);
    }

    /**
     * Removes jwt tokens from database by provided user username.
     * Makes impossible to use these tokens again.
     *
     * @param username {@link String} username that is used for token removing
     */
    @Override
    public void invalidateTokensByUsername(String username) {
        jwtTokenRepository.deleteAllByUsernameAndExpirationIsNull(username);
    }

    /**
     * Transforms provided token to {@link JwtTokenEntity}.
     *
     * @param token {@link String} token to transform
     * @throws InvalidTokenException exception during token handling
     */
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
