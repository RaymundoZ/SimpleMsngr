package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserLoginDto;
import com.raymundo.simplemsngr.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    String logout(HttpServletRequest request) throws InvalidTokenException;

    UserDto login(UserLoginDto userLoginDto, HttpServletRequest request);
}
