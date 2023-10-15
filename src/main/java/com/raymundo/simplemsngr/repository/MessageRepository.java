package com.raymundo.simplemsngr.repository;

import com.raymundo.simplemsngr.entity.MessageEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query(value = "select m from MessageEntity m " +
            "inner join UserEntity s on m.sender = s " +
            "inner join UserEntity r on m.recipient = r " +
            "where (?1 = s and ?2 = r) or " +
            "(?1 = r and ?2 = s)")
    List<MessageEntity> getMessageHistory(UserEntity sender, UserEntity recipient);
}
