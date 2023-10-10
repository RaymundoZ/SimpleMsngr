package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.mapper.UserMapper;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.JwtService;
import com.raymundo.simplemsngr.service.UserService;
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

    @Override
    public UserDto create(UserDto userDto) {
        UserEntity user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setToken(jwtService.generateToken(userDto.username(), userDto.password()));
        return userMapper.toDto(userRepository.save(user));
    }
}
