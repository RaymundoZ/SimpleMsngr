package com.raymundo.simplemsngr.util;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthHelper {

    @Nullable
    String getJwtToken(HttpServletRequest request);

    Authentication authenticate(String username, String password, HttpServletRequest request);
}
