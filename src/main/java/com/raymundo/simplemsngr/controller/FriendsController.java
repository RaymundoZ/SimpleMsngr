package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.basic.SuccessDto;
import com.raymundo.simplemsngr.service.FriendService;
import com.raymundo.simplemsngr.util.exception.FriendOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendService friendService;

    @PostMapping(value = "/add/{username}")
    public ResponseEntity<SuccessDto<String>> addFriend(@PathVariable String username) throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Friend was successfully added",
                        friendService.addFriend(username)
                ), HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/remove/{username}")
    public ResponseEntity<SuccessDto<String>> removeFriend(@PathVariable String username) throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Friend was successfully removed",
                        friendService.removeFriend(username)
                ), HttpStatus.OK
        );
    }

    @GetMapping(value = "/get")
    public ResponseEntity<SuccessDto<List<String>>> getFriends() {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Received a list of friends",
                        friendService.getFriends()
                ), HttpStatus.OK
        );
    }

    @GetMapping(value = "/get/{username}")
    public ResponseEntity<SuccessDto<List<String>>> getFriends(@PathVariable String username) throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        String.format("Received a list of friends for user %s", username),
                        friendService.getFriends(username)
                ), HttpStatus.OK
        );
    }

    @PostMapping(value = "/hide")
    public ResponseEntity<SuccessDto<UserDto>> hideFriends() throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Friend list was successfully hidden",
                        friendService.hideFriends()
                ), HttpStatus.OK
        );
    }

    @PostMapping(value = "/open")
    public ResponseEntity<SuccessDto<UserDto>> openFriends() throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Friend list was successfully opened",
                        friendService.openFriends()
                ), HttpStatus.OK
        );
    }
}
