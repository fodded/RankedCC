package me.fodded.bungeecord.serverhandlers.servers;

import lombok.Getter;
import me.fodded.bungeecord.Main;
import me.fodded.bungeecord.serverhandlers.AbstractServerInfoHandler;
import me.fodded.bungeecord.serverhandlers.BungeeServerHandler;
import me.fodded.core.managers.ranks.Rank;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

public class LobbyServerInfoHandler extends AbstractServerInfoHandler {

    @Getter
    private static final List<LobbyServerInfoHandler> lobbyServerInfoHandlerList = new LinkedList<>();

    public LobbyServerInfoHandler(String serverName, int playersInTotal) {
        super(serverName, playersInTotal);
        lobbyServerInfoHandlerList.add(this);
    }

    public LobbyServerInfoHandler() {
        super();

    }

    public ServerInfo getSuitableServer(String serverCurrentlyOn) {
        Collections.sort(lobbyServerInfoHandlerList, Comparator
                .comparingInt(LobbyServerInfoHandler::getPlayersOnline).reversed());

        // Here we get a server which is not running a game right now and has the most players on atm
        for(LobbyServerInfoHandler serverInfoHandler : lobbyServerInfoHandlerList) {
            // We check if the server we are seeking for is not the same we are currently on
            if(serverInfoHandler.getServerName().equalsIgnoreCase(serverCurrentlyOn)) continue;
            if(serverInfoHandler.getPlayersOnline() >= 100) continue;
            return getProxyServer(serverInfoHandler.getServerName());
        }

        // In case something went wrong we are going to give the first available server from the list
        return getProxyServer(lobbyServerInfoHandlerList.get(0).getServerName());
    }

    public ServerInfo getSuitableServer(UUID playerUniqueId, String serverCurrentlyOn, String serverNamePattern) {
        Collections.sort(lobbyServerInfoHandlerList, Comparator
                .comparingInt(LobbyServerInfoHandler::getPlayersOnline).reversed());

        // Here we get a server which is not running a game right now and has the most players on atm
        for(LobbyServerInfoHandler serverInfoHandler : lobbyServerInfoHandlerList) {
            // We check if the server we are seeking for is not the same we are currently on, and it has the needed name pattern
            // Doing it this way we can find another lobby and specify if the lobby has to be some exact minigames lobby
            if(serverInfoHandler.getServerName().equalsIgnoreCase(serverCurrentlyOn)) continue;
            if(!serverInfoHandler.getServerName().contains(serverNamePattern)) continue;
            if(serverInfoHandler.getPlayersOnline() > 100 && !Rank.hasPermission(Rank.HELPER, playerUniqueId)) continue;
            return getProxyServer(serverInfoHandler.getServerName());
        }

        // In case something went wrong we are going to give the first available server from the list
        return getProxyServer(lobbyServerInfoHandlerList.get(0).getServerName());
    }

    public void clearAndUpdateServerInformation() {
        lobbyServerInfoHandlerList.clear();
        for(Map.Entry serverInfoMap : Main.getInstance().getProxy().getServers().entrySet()) {
            ServerInfo serverInfo = (ServerInfo) serverInfoMap.getValue();

            if(BungeeServerHandler.getUpdatingServers().contains(serverInfo.getName())) {
                continue;
            }

            new LobbyServerInfoHandler(serverInfo.getName(), serverInfo.getPlayers().size());
        }
    }
}
