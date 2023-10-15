package com.raymundo.simplemsngr.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "_message")
@Data
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "message")
    private String message;

    @Column(name = "timestamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @ManyToOne
    private UserEntity sender;

    @ManyToOne
    private UserEntity recipient;
}
