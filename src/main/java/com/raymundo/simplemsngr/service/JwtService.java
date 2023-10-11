package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.exception.InvalidTokenException;

public interface JwtService {

    String generateToken(JwtTokenEntity jwtToken);

    void invalidateToken(String token) throws InvalidTokenException;

    boolean isTokenValid(String token) throws InvalidTokenException;

    JwtTokenEntity parseToken(String token) throws InvalidTokenException;
}
