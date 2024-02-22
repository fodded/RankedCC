package me.fodded.core.user;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetworkUserManager {

    @Getter
    private final Map<UUID, AbstractNetworkPlayer> networkUserMap = new HashMap<>();

    public void addPlayer() {

    }


}
