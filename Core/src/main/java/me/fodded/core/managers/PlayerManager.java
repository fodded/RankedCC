package me.fodded.core.managers;

import lombok.Getter;

import java.util.UUID;

public class PlayerManager {

    @Getter
    private UUID uuid;

    public PlayerManager(UUID uuid) {
        this.uuid = uuid;
    }
}