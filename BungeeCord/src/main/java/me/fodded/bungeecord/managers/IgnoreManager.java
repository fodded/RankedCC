package me.fodded.bungeecord.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreManager {

    private static IgnoreManager instance;
    private static final Map<UUID, Long> floodMap = new HashMap<>();

    public IgnoreManager() {
        instance = this;
    }

    public void addToIgnoreList(ProxiedPlayer playerSentRequest, ProxiedPlayer playerToAddToList) {

    }

    public void removeFromIgnoreList(ProxiedPlayer playerSentRequest, UUID playerUUIDToRemoveFromList) {

    }

    protected boolean isPlayerFlooding(ProxiedPlayer player) {
        if(floodMap.containsKey(player.getUniqueId())) {
            return floodMap.get(player.getUniqueId()) > System.currentTimeMillis();
        }

        floodMap.put(player.getUniqueId(), System.currentTimeMillis() + 2000);
        return false;
    }

    public static IgnoreManager getInstance() {
        if(instance == null) {
            return new IgnoreManager();
        }
        return instance;
    }
}
