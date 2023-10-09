package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.request.UserRequest;
import com.raymundo.simplemsngr.exception.ValidationException;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(value = "/signup")
    public String signUp(@Valid @RequestBody UserRequest userRequest, BindingResult bindingResult) throws ValidationException {
        validate(bindingResult);
        userService.create(userRequest);
        return jwtService.generateToken(userRequest.username(), userRequest.password());
    }

    private void validate(BindingResult bindingResult) throws ValidationException {
        if (!bindingResult.hasErrors()) return;
        ValidationException validationException = new ValidationException();
        bindingResult.getAllErrors().forEach(e -> validationException.addMessage(e.getDefaultMessage()));
        throw validationException;
    }
}
