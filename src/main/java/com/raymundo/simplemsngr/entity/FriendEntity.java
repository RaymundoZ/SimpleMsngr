package com.raymundo.simplemsngr.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "_friend")
@Data
public class FriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private UserEntity friend;
}
