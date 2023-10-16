package com.raymundo.simplemsngr.dto;

import com.raymundo.simplemsngr.util.validation.UsernameUnique;
import jakarta.validation.constraints.NotBlank;

public record UserCredentialsDto(

        @NotBlank(message = "The 'username' attribute should not be null")
        @UsernameUnique(message = "This username is already in use")
        String username,

        @NotBlank(message = "The 'password' attribute should not be null")
        String password
) {
}
