package com.raymundo.simplemsngr.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "_jwt_token")
@Data
public class JwtTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "expiration")
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime expiration;

    @Transient
    private Boolean isExpired;

    public Boolean getIsExpired() {
        return LocalDateTime.now().isBefore(expiration);
    }
}
