package com.raymundo.simplemsngr.security;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.service.JwtService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final List<String> WHITELIST = List.of(
            "/auth/sign_up",
            "/user/verify_email"
    );

    private final JwtService jwtService;
    private final SecurityContextHolderStrategy holderStrategy;
    private final AuthenticationManager authManager;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (WHITELIST.stream().anyMatch(s -> request.getServletPath().startsWith(s))) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getJwtToken(request);
        if (token != null && jwtService.isTokenValid(token)) {
            JwtTokenEntity jwtToken = jwtService.parseToken(token);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(jwtToken.getUsername(), jwtToken.getPassword());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            Authentication authentication = authManager
                    .authenticate(authToken);
            SecurityContext securityContext = holderStrategy.createEmptyContext();
            securityContext.setAuthentication(authentication);
            holderStrategy.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }

    @Nullable
    private String getJwtToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.substring(7);
    }
}
