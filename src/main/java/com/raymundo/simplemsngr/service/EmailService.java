package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.entity.UserEntity;

public interface EmailService {

    void sendAuthVerificationEmail(UserEntity user);
}
