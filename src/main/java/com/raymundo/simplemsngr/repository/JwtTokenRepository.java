package com.raymundo.simplemsngr.repository;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, UUID> {

    void deleteAllByUsernameAndExpirationIsNull(String username);
}
