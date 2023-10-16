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

/**
 * Default implementation of {@link AuthHelper}.
 * Utility bean for security purposes.
 *
 * @author RaymundoZ
 */
@Component
@RequiredArgsConstructor
public class AuthHelperImpl implements AuthHelper {

    private final AuthenticationManager authManager;
    private final WebAuthenticationDetailsSource detailsSource;

    /**
     * Fetches authorization header from {@link HttpServletRequest}.
     * Returns null if header was not found.
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String} jwt token
     */
    @Nullable
    @Override
    public String getJwtToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.substring(7);
    }

    /**
     * Performs authentication using {@link AuthenticationManager} and
     * returns a populated {@link Authentication} object.
     *
     * @param username {@link String}
     * @param password {@link String}
     * @param request  {@link HttpServletRequest}
     * @return {@link Authentication} populated object
     */
    @Override
    public Authentication authenticate(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authToken.setDetails(detailsSource.buildDetails(request));
        return authManager.authenticate(authToken);
    }
}
