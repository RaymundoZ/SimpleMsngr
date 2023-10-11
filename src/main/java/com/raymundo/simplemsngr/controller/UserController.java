package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.basic.SuccessDto;
import com.raymundo.simplemsngr.exception.EmailVerificationException;
import com.raymundo.simplemsngr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/verify_email/{token}")
    public ResponseEntity<SuccessDto<String>> verifyEmail(@PathVariable String token) throws EmailVerificationException {
        return new ResponseEntity<>(
                new SuccessDto<>(HttpStatus.OK.value(),
                        "Email was successfully verified",
                        userService.verifyEmail(token)), HttpStatus.OK
        );
    }
}
