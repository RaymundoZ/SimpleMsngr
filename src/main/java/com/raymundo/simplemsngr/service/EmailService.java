package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserDto;

public interface EmailService {

    void sendAuthVerificationEmail(UserDto user);
}
