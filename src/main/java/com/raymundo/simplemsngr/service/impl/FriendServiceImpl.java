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

/**
 * Default implementation of {@link FriendService}.
 * Service that is responsible for operations connected with user and his friend list.
 *
 * @author RaymundoZ
 */
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final SecurityContextHolderStrategy holderStrategy;
    private final UserMapper userMapper;

    /**
     * Creates {@link FriendEntity} based on provided friend username and
     * current username and saves it to database.
     *
     * @param friendUsername username of user that you want
     *                       to add to the current user's friend list
     * @return friend's username
     * @throws FriendOperationException exception during operations with friend list
     */
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

    /**
     * Searches {@link FriendEntity} in database, if exists removes it.
     *
     * @param friendUsername username of user that you want
     *                       to remove from the current user's friend list
     * @return {@link String} friend's username
     * @throws FriendOperationException exception during operations with friend list
     */
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

    /**
     * Gets a {@link List} of friends usernames of the provided user username.
     *
     * @param username username of user whose friend list you need to get
     * @return {@link List} of friends usernames
     * @throws FriendOperationException exception during operations with friend list
     */
    @Override
    public List<String> getFriends(String username) throws FriendOperationException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (!user.getFriendsVisible())
            throw new FriendOperationException("The user has hidden his friend list");

        return friendRepository.findAllByUser(user)
                .stream().map(f -> f.getFriend().getUsername()).toList();
    }

    /**
     * Gets a {@link List} of friends usernames of the current user.
     *
     * @return {@link List} of friends usernames
     */
    @Override
    public List<String> getFriends() {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        return friendRepository.findAllByUser(user)
                .stream().map(f -> f.getFriend().getUsername()).toList();
    }

    /**
     * Gets current user from {@link SecurityContextHolderStrategy} and invokes
     * {@link UserEntity#setFriendsVisible(Boolean visible)} with false value.
     * Makes impossible to get current user's friend list for other users.
     *
     * @return {@link UserDto} of current user
     * @throws FriendOperationException exception during operations with friend list
     */
    @Override
    public UserDto hideFriends() throws FriendOperationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        if (!user.getFriendsVisible())
            throw new FriendOperationException("Your friend list is already hidden");

        user.setFriendsVisible(false);
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Gets current user from {@link SecurityContextHolderStrategy} and invokes
     * {@link UserEntity#setFriendsVisible(Boolean visible)} with true value.
     * Make possible to get current user's friend list for other users.
     *
     * @return {@link UserDto} of current user
     * @throws FriendOperationException exception during operations with friend list
     */
    @Override
    public UserDto openFriends() throws FriendOperationException {
        UserEntity user = (UserEntity) holderStrategy.getContext().getAuthentication().getPrincipal();

        if (user.getFriendsVisible())
            throw new FriendOperationException("Your friend list is already opened");

        user.setFriendsVisible(true);
        return userMapper.toDto(userRepository.save(user));
    }
}
