package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.exception.EmailVerificationException;
import com.raymundo.simplemsngr.exception.InvalidTokenException;
import com.raymundo.simplemsngr.mapper.UserMapper;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.EmailService;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.service.UserService;
import com.raymundo.simplemsngr.util.EmailStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public UserDto create(UserDto userDto) {
        UserEntity user = userMapper.toEntity(userDto);

        JwtTokenEntity jwtToken = new JwtTokenEntity();
        jwtToken.setUsername(userDto.username());
        jwtToken.setPassword(userDto.password());
        jwtToken.setEmail(userDto.email());
        jwtToken.setExpiration(null);

        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setToken(jwtService.generateToken(jwtToken));
        user.setEmailStatus(EmailStatus.UNVERIFIED);
        emailService.sendAuthVerificationEmail(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public String verifyEmail(String token) throws EmailVerificationException, InvalidTokenException {
        String email = jwtService.parseToken(token).getEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailVerificationException("Incorrect verification link"));
        user.setEmailStatus(EmailStatus.VERIFIED);
        userRepository.save(user);
        jwtService.invalidateToken(token);
        return email;
    }
}
