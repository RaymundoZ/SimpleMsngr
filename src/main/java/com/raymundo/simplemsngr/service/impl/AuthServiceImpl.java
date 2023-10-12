package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.UserLoginDto;
import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.mapper.UserMapper;
import com.raymundo.simplemsngr.service.AuthService;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.util.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthHelper authHelper;
    private final JwtService jwtService;
    private final SecurityContextLogoutHandler logoutHandler;
    private final UserMapper userMapper;
    private final SecurityContextHolderStrategy holderStrategy;

    @Override
    @Transactional
    public String logout(HttpServletRequest request) {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        jwtService.invalidateTokensByUsername(user.getUsername());
        logoutHandler.logout(request, null, null);
        return user.getUsername();
    }

    @Override
    public UserDto login(UserLoginDto userLoginDto, HttpServletRequest request) {
        Authentication authentication = authHelper.authenticate(userLoginDto.username(),
                userLoginDto.password(), request);
        UserEntity user = (UserEntity) authentication.getPrincipal();

        JwtTokenEntity jwtToken = new JwtTokenEntity();
        jwtToken.setUsername(userLoginDto.username());
        jwtToken.setPassword(userLoginDto.password());
        jwtToken.setEmail(user.getEmail());
        jwtToken.setExpiration(null);

        String token = jwtService.generateToken(jwtToken);
        user.setToken(token);

        return userMapper.toDto(user);
    }
}
