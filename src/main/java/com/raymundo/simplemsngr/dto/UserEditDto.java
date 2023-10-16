package com.raymundo.simplemsngr.dto;

import com.raymundo.simplemsngr.util.validation.EmailUnique;

public record UserEditDto(

        String name,

        String surname,

        @EmailUnique(message = "This email is already in use")
        String email
) {
}
