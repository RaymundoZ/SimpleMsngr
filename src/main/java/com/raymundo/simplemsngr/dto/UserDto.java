package com.raymundo.simplemsngr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raymundo.simplemsngr.util.EmailStatus;
import com.raymundo.simplemsngr.util.validation.EmailUnique;
import com.raymundo.simplemsngr.util.validation.UsernameUnique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserDto(

        @NotEmpty(message = "The 'name' attribute should not be null")
        String name,

        @NotEmpty(message = "The 'surname' attribute should not be null")
        String surname,

        @NotEmpty(message = "The 'email' attribute should not be null")
        @Email(message = "The 'email' attribute should be valid")
        @EmailUnique(message = "This email is already in use")
        String email,

        @NotEmpty(message = "The 'username' attribute should not be null")
        @UsernameUnique(message = "This username is already in use")
        String username,

        @NotEmpty(message = "The 'password' attribute should not be null")
        @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @Schema(hidden = true)
        @JsonIgnore
        String token,

        @JsonProperty(value = "friends_visible", access = JsonProperty.Access.READ_ONLY)
        Boolean friendsVisible,

        @JsonProperty(value = "email_status", access = JsonProperty.Access.READ_ONLY)
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        EmailStatus emailStatus

) {

}
