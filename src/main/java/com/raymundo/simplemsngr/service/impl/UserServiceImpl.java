package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserCredentialsDto;
import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserEditDto;
import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.exception.AccountActivationException;
import com.raymundo.simplemsngr.exception.EmailVerificationException;
import com.raymundo.simplemsngr.exception.InvalidTokenException;
import com.raymundo.simplemsngr.mapper.UserMapper;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.AuthService;
import com.raymundo.simplemsngr.service.EmailService;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.service.UserService;
import com.raymundo.simplemsngr.util.EmailStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
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
    private final SecurityContextHolderStrategy holderStrategy;
    private final AuthService authService;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity user = userMapper.toEntity(userDto);
        user.setIsEnabled(true);

        JwtTokenEntity jwtToken = new JwtTokenEntity();
        jwtToken.setUsername(userDto.username());
        jwtToken.setPassword(userDto.password());
        jwtToken.setEmail(userDto.email());
        jwtToken.setExpiration(null);

        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setToken(jwtService.generateToken(jwtToken));
        user.setEmailStatus(EmailStatus.UNVERIFIED);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto editUser(UserEditDto userEditDto) {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        String name = (userEditDto.name() == null || userEditDto.name().isBlank()) ? user.getName() : userEditDto.name();
        String surname = (userEditDto.surname() == null || userEditDto.surname().isBlank()) ? user.getSurname() : userEditDto.surname();
        String email = (userEditDto.email() == null || userEditDto.email().isBlank()) ? user.getEmail() : userEditDto.email();

        boolean hasEmailChanged = !user.getEmail().equals(email);

        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        if (hasEmailChanged)
            user.setEmailStatus(EmailStatus.UNVERIFIED);

        userRepository.save(user);

        if (hasEmailChanged)
            sendVerificationEmail();

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto editCreds(UserCredentialsDto userCredentialsDto) {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        JwtTokenEntity jwtToken = new JwtTokenEntity();
        jwtToken.setUsername(userCredentialsDto.username());
        jwtToken.setPassword(userCredentialsDto.password());
        jwtToken.setEmail(user.getEmail());
        jwtToken.setExpiration(null);

        jwtService.invalidateTokensByUsername(user.getUsername());

        user.setUsername(userCredentialsDto.username());
        user.setPassword(passwordEncoder.encode(userCredentialsDto.password()));
        user.setToken(jwtService.generateToken(jwtToken));

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public String disableAccount(HttpServletRequest request) {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        user.setIsEnabled(false);
        emailService.sendAccountDisableEmail();
        userRepository.save(user);
        authService.logout(request);
        return user.getEmail();
    }

    @Override
    @Transactional
    public String enableAccount(String token) throws InvalidTokenException, AccountActivationException {
        String email = jwtService.parseToken(token).getEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccountActivationException("Incorrect account activation link"));
        user.setIsEnabled(true);
        userRepository.save(user);
        jwtService.invalidateToken(token);
        return user.getUsername();
    }

    @Override
    @Transactional
    public String verifyEmail(String token) throws EmailVerificationException, InvalidTokenException {
        String email = jwtService.parseToken(token).getEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailVerificationException("Incorrect verification link"));
        user.setEmailStatus(EmailStatus.VERIFIED);
        userRepository.save(user);
        jwtService.invalidateToken(token);
        return email;
    }

    @Override
    public String sendVerificationEmail() {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        emailService.sendAuthVerificationEmail();
        return user.getEmail();
    }
}
