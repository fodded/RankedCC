package me.fodded.bungeecord.listeners;

import me.fodded.bungeecord.serverhandlers.BungeeServerHandler;
import me.fodded.bungeecord.serverhandlers.servers.LobbyServerInfoHandler;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerConnectListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(BungeeServerHandler.getInstance().isServerWhitelisted()) {
            if(BungeeServerHandler.getInstance().getBungeeWiteList().contains(event.getPlayer().getName())) {
                return;
            }
            event.getPlayer().disconnect(StringUtils.format("&cThe server is not available at moment"));
        }

        /*
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());

                LobbyServerInfoHandler lobbyServerInfoHandler = new LobbyServerInfoHandler();
                lobbyServerInfoHandler.clearAndUpdateServerInformation();

                ServerInfo serverInfo = lobbyServerInfoHandler.getSuitableServer(player.getUniqueId(), "null", generalStats.getLastLobby());
                if(!player.getServer().getInfo().getName().equalsIgnoreCase(serverInfo.getName())) {
                    player.connect(serverInfo);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);*/
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());

        for(ProxiedPlayer eachPlayer : ProxyServer.getInstance().getPlayers()) {
            if(!generalStats.getFriendList().contains(eachPlayer.getUniqueId())) {
                continue;
            }

            String prefix = StringUtils.getPlayerPrefix(player);
            eachPlayer.sendMessage(StringUtils.format(
                    StringUtils.getMessage(eachPlayer, "friends.player-quit-server").replace("%player%", prefix))
            );
        }
    }

    private List<String> markedWords = new ArrayList<>(Arrays.asList("closed", "full", "shutdown", "restart", "went down"));

    @EventHandler
    public void onKickEvent(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo serverInfo = event.getKickedFrom();

        if(event.isCancelled()) {
            return;
        }

        for(String oneMarkedWord : markedWords) {
            if (!BaseComponent.toLegacyText(event.getKickReasonComponent()).contains(oneMarkedWord)) {
                LobbyServerInfoHandler lobbyServerInfoHandler = new LobbyServerInfoHandler();
                lobbyServerInfoHandler.clearAndUpdateServerInformation();

                event.setCancelled(true);
                player.connect(
                        lobbyServerInfoHandler.getSuitableServer(player.getUniqueId(), player.getServer().getInfo().getName(), "Main")
                );
            }
        }
    }
}
