package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserLoginDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    String logout(HttpServletRequest request);

    UserDto login(UserLoginDto userLoginDto, HttpServletRequest request);
}
