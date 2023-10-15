package com.raymundo.simplemsngr.websocket;

import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HandshakeInterceptorImpl implements HandshakeInterceptor {

    private final UserRepository userRepository;
    private final FriendService friendService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String path = request.getURI().getPath();
        String recipientUsername = path.substring(path.lastIndexOf('/') + 1);

        Optional<UserEntity> recipient = userRepository.findByUsername(recipientUsername);

        UserEntity sender = (UserEntity) ((UsernamePasswordAuthenticationToken) request.getPrincipal()).getPrincipal();
        if (recipient.isEmpty() || sender.getUsername().equals(recipientUsername)) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().add(HttpHeaders.WARNING, "Invalid username");
            return false;
        }

        if (friendService.getFriends(recipientUsername).stream().noneMatch(s -> s.equals(sender.getUsername()))) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().add(HttpHeaders.WARNING, String.format("You are not in friend list of user %s", recipientUsername));
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
