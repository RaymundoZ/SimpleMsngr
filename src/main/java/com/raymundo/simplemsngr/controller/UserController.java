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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserController", description = "Responsible for operations connected with user and his profile")
@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Verifies user's email")
    @GetMapping(value = "/verify_email/{token}")
    public ResponseEntity<SuccessDto<String>> verifyEmail(@PathVariable
                                                          @Parameter(description = "One-use jwt token to verify email")
                                                          String token) throws EmailVerificationException, InvalidTokenException {
        return new ResponseEntity<>(
                new SuccessDto<>(HttpStatus.OK.value(),
                        "Email was successfully verified",
                        userService.verifyEmail(token)), HttpStatus.OK
        );
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Sends verification email for current user")
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

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Edits current user's profile info")
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

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Edits current user's credentials")
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

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Disables current user's account")
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

    @Operation(summary = "Enables current user's account")
    @GetMapping(value = "/enable/{token}")
    public ResponseEntity<SuccessDto<String>> enableAccount(@PathVariable
                                                            @Parameter(description = "One-use jwt token to enable account")
                                                            String token) throws InvalidTokenException, AccountActivationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Account was successfully enabled",
                        userService.enableAccount(token)
                ), HttpStatus.OK
        );
    }
}
