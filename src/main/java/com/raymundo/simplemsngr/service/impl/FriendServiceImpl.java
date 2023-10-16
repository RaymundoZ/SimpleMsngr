package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.entity.FriendEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.mapper.UserMapper;
import com.raymundo.simplemsngr.repository.FriendRepository;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.FriendService;
import com.raymundo.simplemsngr.util.exception.FriendOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final SecurityContextHolderStrategy holderStrategy;
    private final UserMapper userMapper;

    @Override
    public String addFriend(String friendUsername) throws FriendOperationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        UserEntity friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (user.getUsername().equals(friend.getUsername()))
            throw new FriendOperationException("You can not add to friend list yourself");

        if (getFriends().stream().anyMatch(s -> s.equals(friend.getUsername())))
            throw new FriendOperationException(String.format("User %s is already in your friend list", friend.getUsername()));

        FriendEntity friendEntity = new FriendEntity();
        friendEntity.setUser(user);
        friendEntity.setFriend(friend);
        friendRepository.save(friendEntity);
        return friend.getUsername();
    }

    @Override
    public String removeFriend(String friendUsername) throws FriendOperationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();
        UserEntity friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        FriendEntity friendEntity = friendRepository.findByUserAndFriend(user, friend)
                .orElseThrow(() -> new FriendOperationException(String.format("User %s is not in your friend list", friend.getUsername())));

        friendRepository.delete(friendEntity);
        return friend.getUsername();
    }

    @Override
    public List<String> getFriends(String username) throws FriendOperationException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (!user.getFriendsVisible())
            throw new FriendOperationException("The user has hidden his friend list");

        return friendRepository.findAllByUser(user)
                .stream().map(f -> f.getFriend().getUsername()).toList();
    }

    @Override
    public List<String> getFriends() {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        return friendRepository.findAllByUser(user)
                .stream().map(f -> f.getFriend().getUsername()).toList();
    }

    @Override
    public UserDto hideFriends() throws FriendOperationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        if (!user.getFriendsVisible())
            throw new FriendOperationException("Your friend list is already hidden");

        user.setFriendsVisible(false);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto openFriends() throws FriendOperationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        if (user.getFriendsVisible())
            throw new FriendOperationException("Your friend list is already opened");

        user.setFriendsVisible(true);
        return userMapper.toDto(userRepository.save(user));
    }
}
