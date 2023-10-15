package com.raymundo.simplemsngr.websocket;

import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.repository.UserRepository;
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

@Component
@RequiredArgsConstructor
public class HandshakeInterceptorImpl implements HandshakeInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String path = request.getURI().getPath();
        String username = path.substring(path.lastIndexOf('/') + 1);
        UserEntity currentUser = (UserEntity) ((UsernamePasswordAuthenticationToken) request.getPrincipal()).getPrincipal();
        if (userRepository.findByUsername(username).isEmpty() || currentUser.getUsername().equals(username)) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().add(HttpHeaders.WARNING, "Invalid username");
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
