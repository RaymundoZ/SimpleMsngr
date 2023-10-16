package com.raymundo.simplemsngr.service.impl;

import com.raymundo.simplemsngr.dto.MessageDto;
import com.raymundo.simplemsngr.entity.MessageEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import com.raymundo.simplemsngr.repository.MessageRepository;
import com.raymundo.simplemsngr.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

/**
 * Default implementation of {@link MessageService}.
 * Service that is responsible for operations with messages.
 * Used for web sockets.
 *
 * @author RaymundoZ
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    /**
     * Saves {@link MessageEntity} to database based on provided parameters.
     *
     * @param message   {@link String}
     * @param sender    {@link UserEntity}
     * @param recipient {@link UserEntity}
     * @return {@link MessageDto} messageDto
     */
    @Override
    public MessageDto saveMessage(String message, UserEntity sender, UserEntity recipient) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessage(message);
        messageEntity.setSender(sender);
        messageEntity.setRecipient(recipient);
        messageEntity.setTimestamp(LocalDateTime.now());
        messageRepository.save(messageEntity);
        return getMessageDto(messageEntity);
    }

    /**
     * Returns {@link List} of messages based on provided sender and recipient.
     *
     * @param sender    {@link UserEntity}
     * @param recipient {@link UserEntity}
     * @return {@link List<MessageDto>} list of messages
     */
    @Override
    public List<MessageDto> getMessageHistory(UserEntity sender, UserEntity recipient) {
        return messageRepository.getMessageHistory(sender, recipient)
                .stream().map(this::getMessageDto).toList();
    }

    private MessageDto getMessageDto(MessageEntity message) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm:ss")
                .toFormatter();
        return new MessageDto(message.getSender().getUsername(), message.getMessage(),
                message.getTimestamp().format(formatter));
    }
}
