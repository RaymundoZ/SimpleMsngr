package com.raymundo.simplemsngr.controller;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.dto.basic.SuccessDto;
import com.raymundo.simplemsngr.service.FriendService;
import com.raymundo.simplemsngr.util.exception.FriendOperationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that is responsible for operations connected with user and his friend list.
 *
 * @author RaymundoZ
 */
@Tag(name = "FriendsController", description = "Responsible for operations connected with user and his friend list")
@RestController
@RequestMapping(value = "/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendService friendService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Adds user in path to current user's friend list")
    @PostMapping(value = "/add/{username}")
    public ResponseEntity<SuccessDto<String>> addFriend(@PathVariable
                                                        @Parameter(description = "Username to add to current user's friend list")
                                                        String username) throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Friend was successfully added",
                        friendService.addFriend(username)
                ), HttpStatus.OK
        );
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Removes user in path from current user's friend list")
    @DeleteMapping(value = "/remove/{username}")
    public ResponseEntity<SuccessDto<String>> removeFriend(@PathVariable
                                                           @Parameter(description = "Username to remove from current user's friend list")
                                                           String username) throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        "Friend was successfully removed",
                        friendService.removeFriend(username)
                ), HttpStatus.OK
        );
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns current user's friend list")
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

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns friend list of user in path")
    @GetMapping(value = "/get/{username}")
    public ResponseEntity<SuccessDto<List<String>>> getFriends(@PathVariable
                                                               @Parameter(description = "Username of user which friend list you want to get")
                                                               String username) throws FriendOperationException {
        return new ResponseEntity<>(
                new SuccessDto<>(
                        HttpStatus.OK.value(),
                        String.format("Received a list of friends for user %s", username),
                        friendService.getFriends(username)
                ), HttpStatus.OK
        );
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Make impossible to get current user's friend list for other users")
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

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Make possible to get current user's friend list for other users")
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
