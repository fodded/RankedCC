package me.fodded.bungeecord.managers.ignore;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface IgnoreRequest {
    void process(ProxiedPlayer playerSentRequest, String playerReceivedRequestName);
}
