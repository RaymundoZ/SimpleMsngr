package com.raymundo.simplemsngr.dto.request;

import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.util.BaseDto;
import com.raymundo.simplemsngr.util.ConvertableToEntity;
import com.raymundo.simplemsngr.validation.UsernameUnique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserRequest(

        @NotEmpty(message = "The 'name' attribute should not be null")
        String name,

        @NotEmpty(message = "The 'surname' attribute should not be null")
        String surname,

        @NotEmpty(message = "The 'email' attribute should not be null")
        @Email(message = "The 'email' attribute should be valid")
        String email,

        @NotEmpty(message = "The 'username' attribute should not be null")
        @UsernameUnique(message = "This username is already in use")
        String username,

        @NotEmpty(message = "The 'password' attribute should not be null")
        String password

) implements BaseDto, ConvertableToEntity<UserEntity> {

    @Override
    public UserEntity toEntity() {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
