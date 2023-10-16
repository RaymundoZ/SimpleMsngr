package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserCredentialsDto;
import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserEditDto;
import com.raymundo.simplemsngr.util.exception.AccountActivationException;
import com.raymundo.simplemsngr.util.exception.EmailVerificationException;
import com.raymundo.simplemsngr.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserDto createUser(UserDto userDto);

    String verifyEmail(String token) throws EmailVerificationException, InvalidTokenException;

    String sendVerificationEmail() throws EmailVerificationException;

    UserDto editUser(UserEditDto userEditDto) throws EmailVerificationException;

    UserDto editCreds(UserCredentialsDto userCredentialsDto);

    String disableAccount(HttpServletRequest request);

    String enableAccount(String token) throws InvalidTokenException, AccountActivationException;
}
