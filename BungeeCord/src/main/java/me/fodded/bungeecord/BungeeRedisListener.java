package me.fodded.bungeecord;

import me.fodded.bungeecord.serverhandlers.servers.LobbyServerInfoHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.redisson.api.listener.MessageListener;

import java.util.UUID;

public class BungeeRedisListener implements MessageListener {

    @Override
    public void onMessage(CharSequence channel, Object msg) {
        UUID playerUniqueId = UUID.fromString(msg.toString().split(":")[0]);
        String serverNamePatter = msg.toString().split(":")[1];

        LobbyServerInfoHandler lobbyServerInfoHandler = new LobbyServerInfoHandler();
        lobbyServerInfoHandler.clearAndUpdateServerInformation();

        System.out.println("pattern " + serverNamePatter);

        ServerInfo server = lobbyServerInfoHandler.getSuitableServer(serverNamePatter);


        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUniqueId);
        System.out.println("player is in " + player.getServer().getInfo().getName());
        player.connect(server);


    }
}
