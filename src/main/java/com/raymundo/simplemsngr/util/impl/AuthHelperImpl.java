package com.raymundo.simplemsngr.util.impl;

import com.raymundo.simplemsngr.util.AuthHelper;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelperImpl implements AuthHelper {

    private final AuthenticationManager authManager;
    private final WebAuthenticationDetailsSource detailsSource;

    @Nullable
    @Override
    public String getJwtToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.substring(7);
    }

    @Override
    public Authentication authenticate(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authToken.setDetails(detailsSource.buildDetails(request));
        return authManager.authenticate(authToken);
    }
}
