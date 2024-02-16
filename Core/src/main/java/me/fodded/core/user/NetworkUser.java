package me.fodded.core.user;

import me.fodded.core.managers.ranks.Rank;

import java.util.UUID;

public class NetworkUser {

    private final UUID uniqueId;
    private final Rank rank;

    public NetworkUser(UUID uniqueId, Rank rank) {
        this.uniqueId = uniqueId;
        this.rank = rank;
    }

}
