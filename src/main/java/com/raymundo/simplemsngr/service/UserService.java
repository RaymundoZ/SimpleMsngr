package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserCredentialsDto;
import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserEditDto;
import com.raymundo.simplemsngr.exception.AccountActivationException;
import com.raymundo.simplemsngr.exception.EmailVerificationException;
import com.raymundo.simplemsngr.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserDto createUser(UserDto userDto);

    String verifyEmail(String token) throws EmailVerificationException, InvalidTokenException;

    String sendVerificationEmail();

    UserDto editUser(UserEditDto userEditDto);

    UserDto editCreds(UserCredentialsDto userCredentialsDto);

    String disableAccount(HttpServletRequest request);

    String enableAccount(String token) throws InvalidTokenException, AccountActivationException;
}
