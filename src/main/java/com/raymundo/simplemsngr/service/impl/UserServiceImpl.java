package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserCredentialsDto;
import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserEditDto;
import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.mapper.UserMapper;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.AuthService;
import com.raymundo.simplemsngr.service.EmailService;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.service.UserService;
import com.raymundo.simplemsngr.util.EmailStatus;
import com.raymundo.simplemsngr.util.exception.AccountActivationException;
import com.raymundo.simplemsngr.util.exception.EmailVerificationException;
import com.raymundo.simplemsngr.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link UserService}.
 * Service that is responsible for operations with users.
 *
 * @author RaymundoZ
 */
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

    /**
     * Creates new user and saves it to database.
     * Also creates jwt token based on that user.
     *
     * @param userDto {@link UserDto}
     * @return {@link UserDto} user dto
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity user = userMapper.toEntity(userDto);
        user.setIsEnabled(true);
        user.setFriendsVisible(true);

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

    /**
     * Edits user's non-credentials fields based on provided {@link UserEditDto}.
     * If field in {@link UserEditDto} is not provided, it will not be changed.
     * If email has changed, sends verification email.
     *
     * @param userEditDto {@link UserEditDto}
     * @return {@link UserDto} userDto
     * @throws EmailVerificationException exception during operations with email
     */
    @Override
    @Transactional
    public UserDto editUser(UserEditDto userEditDto) throws EmailVerificationException {
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

    /**
     * Edits user's credentials based on provided {@link UserCredentialsDto}.
     *
     * @param userCredentialsDto {@link UserCredentialsDto}
     * @return {@link UserDto} userDto
     */
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

    /**
     * Gets current user by {@link SecurityContextHolderStrategy} and invokes
     * {@link UserEntity#setIsEnabled(Boolean isEnabled)} with false value.
     * Makes this user impossible to login.
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String} user's email
     */
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

    /**
     * Gets current user by from provided jwt token and invokes
     * {@link UserEntity#setIsEnabled(Boolean isEnabled)} with true value.
     * Makes this user possible to login.
     *
     * @param token {@link String}
     * @return {@link String} user's username
     * @throws InvalidTokenException      exception during parsing token
     * @throws AccountActivationException exception occurs if user not found
     */
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

    /**
     * Verifies email by provided jwt token.
     * Invokes {@link UserEntity#setEmailStatus(EmailStatus status)} with VERIFIED value
     * if verification is successful.
     *
     * @param token {@link String}
     * @return {@link String} verified email
     * @throws EmailVerificationException exception occurs if user not found
     * @throws InvalidTokenException      exception during parsing token
     */
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

    /**
     * Sends verification email for the current user
     * if it is not verified yet.
     *
     * @return {@link String} email
     * @throws EmailVerificationException exception occurs if user not found
     */
    @Override
    public String sendVerificationEmail() throws EmailVerificationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        if (user.getEmailStatus().equals(EmailStatus.VERIFIED))
            throw new EmailVerificationException("Email is already verified");

        emailService.sendAuthVerificationEmail();
        return user.getEmail();
    }
}
