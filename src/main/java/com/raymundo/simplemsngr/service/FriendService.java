package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.util.exception.FriendOperationException;

import java.util.List;

public interface FriendService {

    String addFriend(String friendUsername) throws FriendOperationException;

    String removeFriend(String friendUsername) throws FriendOperationException;

    List<String> getFriends(String username) throws FriendOperationException;

    List<String> getFriends();

    UserDto hideFriends() throws FriendOperationException;

    UserDto openFriends() throws FriendOperationException;
}
