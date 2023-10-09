package com.raymundo.simplemsngr.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface JwtService {

    String generateToken(String username, String password);

    UsernamePasswordAuthenticationToken getAuthenticationToken(String token, HttpServletRequest request);
}
