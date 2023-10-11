package com.raymundo.simplemsngr.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface JwtService {

    String generateToken(String username, String password);

    String generateToken(String email);

    UsernamePasswordAuthenticationToken getAuthenticationToken(String token, HttpServletRequest request);

    String getEmailFromToken(String token);
}
