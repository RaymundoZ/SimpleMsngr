package com.raymundo.simplemsngr.service;

public interface EmailService {

    void sendAuthVerificationEmail();

    void sendAccountDisableEmail();
}
