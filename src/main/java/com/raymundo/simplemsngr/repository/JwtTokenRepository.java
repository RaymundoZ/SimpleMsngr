package com.raymundo.simplemsngr.repository;

import com.raymundo.simplemsngr.entity.JwtTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, UUID> {

    void deleteAllByUsernameAndExpirationIsNull(String username);
}
