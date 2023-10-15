package com.raymundo.simplemsngr.repository;

import com.raymundo.simplemsngr.entity.FriendEntity;
import com.raymundo.simplemsngr.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<FriendEntity, UUID> {

    List<FriendEntity> findAllByUser(UserEntity user);

    Optional<FriendEntity> findByUserAndFriend(UserEntity user, UserEntity friend);
}
