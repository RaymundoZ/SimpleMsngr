package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.service.EmailService;
import com.raymundo.simplemsngr.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String SUBJECT = "Email verification";
    private static final String TEXT = "The '%s' user with '%s' email just created an account.\nTo confirm click the link: http%s://%s:%s/user/verify_email/%s\nThe link will be available for 1 hour.";

    @Value(value = "${email-service.domain}")
    private String domain;

    @Value(value = "${email-service.port}")
    private String port;

    @Value(value = "${email-service.use-https}")
    private boolean useHttps;

    private final JavaMailSender mailSender;
    private final JwtService jwtService;

    @Override
    public void sendAuthVerificationEmail(UserDto user) {
        JwtTokenEntity jwtToken = new JwtTokenEntity();
        jwtToken.setUsername(user.username());
        jwtToken.setPassword(user.password());
        jwtToken.setEmail(user.email());
        jwtToken.setExpiration(LocalDateTime.now().plusHours(1));
        jwtToken.setIsValid(true);

        SimpleMailMessage message = new SimpleMailMessage();
        String token = jwtService.generateToken(jwtToken);
        message.setTo(user.email());
        message.setSubject(SUBJECT);
        message.setText(String.format(TEXT, user.username(), user.email(),
                useHttps ? "s" : "", domain, port, token));
        mailSender.send(message);
    }
}
