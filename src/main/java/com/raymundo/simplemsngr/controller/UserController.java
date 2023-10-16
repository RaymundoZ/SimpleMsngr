package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.UserCredentialsDto;
import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserEditDto;
import com.raymundo.simplemsngr.dto.basic.SuccessDto;
import com.raymundo.simplemsngr.service.UserService;
import com.raymundo.simplemsngr.util.GlobalExceptionHandler;
import com.raymundo.simplemsngr.util.exception.AccountActivationException;
import com.raymundo.simplemsngr.util.exception.EmailVerificationException;
import com.raymundo.simplemsngr.util.exception.InvalidTokenException;
import com.raymundo.simplemsngr.util.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/verify_email/{token}")
    public ResponseEntity<SuccessDto<String>> verifyEmail(@PathVariable String token) throws EmailVerificationException, InvalidTokenException {
        return new ResponseEntity<>(
                new SuccessDto<>(HttpStatus.OK.value(),
                        "Email was successfully verified",
                        userService.verifyEmail(token)), HttpStatus.OK
        );
    }

    @PostMapping(value = "/send_email")
    public ResponseEntity<SuccessDto<String>> sendVerificationEmail() throws EmailVerificationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Email was successfully sent",
                        userService.sendVerificationEmail()
                ), HttpStatus.OK
        );
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<SuccessDto<UserDto>> editUser(@Valid @RequestBody UserEditDto userEditDto, BindingResult bindingResult) throws ValidationException, EmailVerificationException {
        if (bindingResult.hasErrors())
            throw new ValidationException(GlobalExceptionHandler.handleValidationResults(bindingResult));

        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "User was successfully edited",
                        userService.editUser(userEditDto)
                ), HttpStatus.OK
        );
    }

    @PutMapping(value = "/edit_creds")
    public ResponseEntity<SuccessDto<UserDto>> editCreds(@Valid @RequestBody UserCredentialsDto credentialsDto, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors())
            throw new ValidationException(GlobalExceptionHandler.handleValidationResults(bindingResult));

        UserDto user = userService.editCreds(credentialsDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, user.token());

        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Credentials were successfully edited",
                        user
                ), headers, HttpStatus.OK
        );
    }

    @PostMapping(value = "/disable")
    public ResponseEntity<SuccessDto<String>> disableAccount(HttpServletRequest request) {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Account was successfully disabled",
                        String.format("Info was sent on %s", userService.disableAccount(request))
                ), HttpStatus.OK
        );
    }

    @GetMapping(value = "/enable/{token}")
    public ResponseEntity<SuccessDto<String>> enableAccount(@PathVariable String token) throws InvalidTokenException, AccountActivationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Account was successfully enabled",
                        userService.enableAccount(token)
                ), HttpStatus.OK
        );
    }
}
