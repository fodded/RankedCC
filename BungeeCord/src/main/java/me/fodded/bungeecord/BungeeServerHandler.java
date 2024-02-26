package me.fodded.bungeecord;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class BungeeServerHandler {

    @Getter
    private final static List<String> updatingServers = new LinkedList<>();

    public BungeeServerHandler() {

    }

}
