package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.exception.EmailVerificationException;

public interface UserService {

    UserDto create(UserDto userDto);

    String verifyEmail(String token) throws EmailVerificationException;
}
