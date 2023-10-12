package com.raymundo.simplemsngr.security;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.exception.InvalidTokenException;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.util.AuthHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityContextHolderStrategy holderStrategy;
    private final AuthHelper authHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = authHelper.getJwtToken(request);
        if (token != null) {

            JwtTokenEntity jwtToken;
            try {
                jwtToken = jwtService.parseToken(token);
            } catch (InvalidTokenException e) {
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authentication;

            try {
                authentication = authHelper.authenticate(jwtToken.getUsername(),
                        jwtToken.getPassword(), request);
            } catch (RuntimeException e) {
                filterChain.doFilter(request, response);
                return;
            }

            SecurityContext securityContext = holderStrategy.createEmptyContext();
            securityContext.setAuthentication(authentication);
            holderStrategy.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }
}
