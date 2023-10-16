package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserLoginDto;
import com.raymundo.simplemsngr.dto.basic.SuccessDto;
import com.raymundo.simplemsngr.service.AuthService;
import com.raymundo.simplemsngr.service.UserService;
import com.raymundo.simplemsngr.util.GlobalExceptionHandler;
import com.raymundo.simplemsngr.util.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that is responsible for authentication operations.
 *
 * @author RaymundoZ
 */
@Tag(name = "AuthController", description = "Responsible for security operations")
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "Registers a new user")
    @PostMapping(value = "/sign_up")
    public ResponseEntity<SuccessDto<UserDto>> signUp(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors())
            throw new ValidationException(GlobalExceptionHandler.handleValidationResults(bindingResult));

        UserDto user = userService.createUser(userDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, user.token());

        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.CREATED.value(),
                        "User was successfully signed up",
                        user
                ), headers, HttpStatus.CREATED
        );
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Invalidates user's jwt tokens")
    @PostMapping(value = "/logout")
    public ResponseEntity<SuccessDto<String>> logout(HttpServletRequest request) {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "User was successfully logged out",
                        authService.logout(request)
                ), HttpStatus.OK
        );
    }

    @Operation(summary = "Returns a new jwt token")
    @PostMapping(value = "/login")
    public ResponseEntity<SuccessDto<UserDto>> login(@Valid @RequestBody UserLoginDto userLoginDto, BindingResult bindingResult, HttpServletRequest request) throws ValidationException {
        if (bindingResult.hasErrors())
            throw new ValidationException(GlobalExceptionHandler.handleValidationResults(bindingResult));

        UserDto userDto = authService.login(userLoginDto, request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, userDto.token());

        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "User was successfully logged in",
                        userDto
                ), headers, HttpStatus.OK
        );
    }
}
