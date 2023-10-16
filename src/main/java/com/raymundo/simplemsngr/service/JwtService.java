package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.util.exception.InvalidTokenException;

public interface JwtService {

    String generateToken(JwtTokenEntity jwtToken);

    void invalidateToken(String token) throws InvalidTokenException;

    void invalidateTokensByUsername(String username);

    JwtTokenEntity parseToken(String token) throws InvalidTokenException;
}
