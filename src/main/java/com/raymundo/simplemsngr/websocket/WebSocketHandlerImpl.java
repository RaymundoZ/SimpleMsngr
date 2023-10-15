package com.raymundo.simplemsngr.websocket;

import com.raymundo.simplemsngr.dto.MessageDto;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.repository.UserRepository;
import com.raymundo.simplemsngr.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final UserRepository userRepository;
    private final MessageService messageService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MessageDto messageDto = messageService.saveMessage(message.getPayload(), getSender(session), getRecipient(session));
        for (WebSocketSession s : getSuitableSessions(session))
            s.sendMessage(getTextMessage(messageDto));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sendMessageHistory(session);
        sendJoinNotification(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sendLeaveNotification(session);
    }

    private UserEntity getSender(WebSocketSession session) {
        return (UserEntity) ((UsernamePasswordAuthenticationToken) session.getPrincipal()).getPrincipal();
    }

    private UserEntity getRecipient(WebSocketSession session) {
        String path = session.getUri().getPath();
        String recipientUsername = path.substring(path.lastIndexOf('/') + 1);
        return userRepository.findByUsername(recipientUsername).get();
    }

    private List<WebSocketSession> getSuitableSessions(WebSocketSession session) {
        return sessions.stream()
                .filter(s -> (getSender(session).getUsername().equals(getRecipient(s).getUsername()) &&
                        (getSender(s).getUsername().equals(getRecipient(session).getUsername()))))
                .toList();
    }

    private TextMessage getTextMessage(MessageDto message) {
        return new TextMessage(String.format("%s: %s %s", message.username(), message.message(), message.timestamp()));
    }

    private void sendMessageHistory(WebSocketSession session) throws IOException {
        List<MessageDto> messages = messageService.getMessageHistory(getSender(session), getRecipient(session));

        for (MessageDto message : messages)
            session.sendMessage(getTextMessage(message));
    }

    private void sendJoinNotification(WebSocketSession session) throws IOException {
        for (WebSocketSession s : getSuitableSessions(session))
            s.sendMessage(new TextMessage(String.format("%s has joined the chat!", getSender(session).getUsername())));
    }

    private void sendLeaveNotification(WebSocketSession session) throws IOException {
        for (WebSocketSession s : getSuitableSessions(session))
            s.sendMessage(new TextMessage(String.format("%s has left the chat!", getSender(session).getUsername())));
    }
}
