package me.fodded.bungeecord.managers.friends;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface IFriendRequest {

    void processRequest(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest);

}
