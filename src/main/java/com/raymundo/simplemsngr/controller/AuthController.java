package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.basic.SuccessDto;
import com.raymundo.simplemsngr.exception.ValidationException;
import com.raymundo.simplemsngr.service.UserService;
import com.raymundo.simplemsngr.util.GlobalExceptionHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping(value = "/sign_up")
    public ResponseEntity<SuccessDto<UserDto>> signUp(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors())
            throw new ValidationException(GlobalExceptionHandler.handleValidationResults(bindingResult));

        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.CREATED.value(),
                        "User was successfully signed up",
                        userService.create(userDto)
                ), HttpStatus.CREATED
        );
    }
}
