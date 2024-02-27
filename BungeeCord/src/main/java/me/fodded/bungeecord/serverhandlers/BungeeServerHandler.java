package me.fodded.bungeecord.serverhandlers;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public class BungeeServerHandler {

    @Getter
    private final static List<String> updatingServers = new LinkedList<>();
    @Getter
    private final List<String> bungeeWiteList = new LinkedList<>();

    private static BungeeServerHandler instance;

    @Getter @Setter
    private boolean isServerWhitelisted = false;

    public BungeeServerHandler() {
        instance = this;
    }

    public void addToUpdatingServersList(String serverName) {
        updatingServers.add(serverName);
    }

    public void removeFromUpdatingServersList(String serverName) {
        updatingServers.remove(serverName);
    }

    public void addToBungeeWhiteList(String playerName) {
        bungeeWiteList.add(playerName);
    }

    public void removeFromBungeeWhiteList(String playerName) {
        bungeeWiteList.remove(playerName);
    }

    public static BungeeServerHandler getInstance() {
        if(instance == null) {
            return new BungeeServerHandler();
        }
        return instance;
    }
}
