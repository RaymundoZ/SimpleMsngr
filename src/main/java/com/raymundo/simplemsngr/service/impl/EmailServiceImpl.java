package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.service.EmailService;
import com.raymundo.simplemsngr.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Default implementation of {@link EmailService}.
 * Service that is responsible for email messages sending.
 *
 * @author RaymundoZ
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String VERIFICATION_SUBJECT = "Email verification";
    private static final String VERIFICATION_TEXT = "The '%s' user with '%s' email just created an account.\nTo confirm click the link: http%s://%s:%s/user/verify_email/%s\nThe link will be available for 1 hour.";

    private static final String ACCOUNT_DISABLE_SUBJECT = "Your account is about to be deleted";

    private static final String ACCOUNT_DISABLE_TEXT = "The '%s' user with '%s' email just requested for account deleting.\nIf you want to restore your account click the link: http%s://%s:%s/user/enable/%s\nThe link will be available for 1 month.";

    @Value(value = "${email-service.domain}")
    private String domain;

    @Value(value = "${email-service.port}")
    private String port;

    @Value(value = "${email-service.use-https}")
    private boolean useHttps;

    private final JavaMailSender mailSender;
    private final JwtService jwtService;
    private final SecurityContextHolderStrategy holderStrategy;

    /**
     * Gets current user from {@link SecurityContextHolderStrategy}.
     * Based on the received principal generates jwt token and saves to database.
     * Sends email message with verification link to the current user's email.
     */
    @Override
    public void sendAuthVerificationEmail() {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        JwtTokenEntity jwtToken = getJwtToken(user, LocalDateTime.now().plusHours(1));
        String token = jwtService.generateToken(jwtToken);
        SimpleMailMessage message = getMailMessage(user, token, VERIFICATION_SUBJECT, VERIFICATION_TEXT);
        mailSender.send(message);
    }

    /**
     * Gets current user from {@link SecurityContextHolderStrategy}.
     * Based on the received principal generates jwt token and saves to database.
     * Sends email message with account restore link to the current user's email.
     */
    @Override
    public void sendAccountDisableEmail() {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        JwtTokenEntity jwtToken = getJwtToken(user, LocalDateTime.now().plusMonths(1));
        String token = jwtService.generateToken(jwtToken);
        SimpleMailMessage message = getMailMessage(user, token, ACCOUNT_DISABLE_SUBJECT, ACCOUNT_DISABLE_TEXT);
        mailSender.send(message);
    }

    private JwtTokenEntity getJwtToken(UserEntity user, LocalDateTime expiration) {
        JwtTokenEntity jwtToken = new JwtTokenEntity();
        jwtToken.setUsername(user.getUsername());
        jwtToken.setPassword(user.getPassword());
        jwtToken.setEmail(user.getEmail());
        jwtToken.setExpiration(expiration);
        return jwtToken;
    }

    private SimpleMailMessage getMailMessage(UserEntity user, String token, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(String.format(text, user.getUsername(), user.getEmail(),
                useHttps ? "s" : "", domain, port, token));
        return message;
    }
}
