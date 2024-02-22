package me.fodded.core.user;

import lombok.Getter;

import java.util.UUID;

public abstract class AbstractNetworkPlayer {

    @Getter
    private final UUID uniqueId;

    public AbstractNetworkPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
