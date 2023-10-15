package com.raymundo.simplemsngr.service;

import com.raymundo.simplemsngr.dto.MessageDto;
import com.raymundo.simplemsngr.entity.UserEntity;

import java.util.List;

public interface MessageService {

    MessageDto saveMessage(String message, UserEntity sender, UserEntity recipient);

    List<MessageDto> getMessageHistory(UserEntity sender, UserEntity recipient);
}
