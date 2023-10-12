package com.raymundo.simplemsngr.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserLoginDto(

        @NotEmpty(message = "The 'username' attribute should not be null")
        String username,

        @NotEmpty(message = "The 'password' attribute should not be null")
        String password
) {
}
