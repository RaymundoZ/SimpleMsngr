package com.raymundo.simplemsngr.config;

import com.raymundo.simplemsngr.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String[] PERMIT_ALL_ENDPOINTS = {
            "/auth/sign_up",
            "/user/verify_email/*",
            "/user/enable/*",
            "/auth/login"
    };

    private static final String[] AUTHENTICATED_ENDPOINTS = {
            "/auth/logout",
            "/user/send_email",
            "/user/edit",
            "/user/edit_creds",
            "/user/disable",
            "/chat/*",
            "/friends/add/*",
            "/friends/get",
            "/friends/get/*",
            "/friends/hide",
            "/friends/open",
            "/friends/remove/*"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtFilter jwtFilter) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(PERMIT_ALL_ENDPOINTS).permitAll();
                    auth.requestMatchers(AUTHENTICATED_ENDPOINTS).authenticated();
                    auth.anyRequest().denyAll();
                })
                .build();
    }
}
