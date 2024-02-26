package me.fodded.bungeecord;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.model.Redis;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

@Getter
public class MinigameServerInfoHandler {

    @Getter
    private static final List<MinigameServerInfoHandler> minigameServerInfoHandlers = new LinkedList<>();

    private final String serverName;
    private final int playersQueuing, playersInTotal;

    public MinigameServerInfoHandler(String serverName, int playersQueuing, int playersInTotal) {
        this.serverName = serverName;
        this.playersQueuing = playersQueuing;
        this.playersInTotal = playersInTotal;

        minigameServerInfoHandlers.add(this);
    }

    public static MinigameServerInfoHandler getSuitableServer(String serverCurrentlyOn) {
        Collections.sort(minigameServerInfoHandlers, Comparator
                .comparingInt(MinigameServerInfoHandler::getPlayersQueuing).reversed()
                .thenComparing(MinigameServerInfoHandler::getPlayersInTotal));

        // Here we get a server which is not running a game right now and has the most players on atm
        for(MinigameServerInfoHandler serverInfoHandler : minigameServerInfoHandlers) {
            // We check if the server we are seeking for is not the same we are currently on, and it is not running a game
            if(serverInfoHandler.serverName.equalsIgnoreCase(serverCurrentlyOn)) continue;
            return serverInfoHandler;
        }

        // In case something went wrong we are going to give the first available server from the list
        return minigameServerInfoHandlers.get(0);
    }

    public static void clearAndUpdateServerInformation() {
        Redis redis = Core.getInstance().getRedis();
        Map<String, Integer> minigameServersMap = redis.getRedissonClient().getMap("minigame-servers");

        minigameServerInfoHandlers.clear();
        for(Map.Entry entry : minigameServersMap.entrySet()) {
            String serverName = (String) entry.getKey();
            int playersQueuing = (Integer) entry.getValue();

            if(BungeeServerHandler.getUpdatingServers().contains(serverName)) {
                continue;
            }

            new MinigameServerInfoHandler(serverName, playersQueuing, getProxyServer(serverName).getPlayers().size());
        }
    }

    private static ServerInfo getProxyServer(String serverName) {
        for (Map.Entry entry : ProxyServer.getInstance().getServers().entrySet()) {
            ServerInfo proxyServer = (ServerInfo) entry.getValue();
            if(proxyServer.getName().equalsIgnoreCase(serverName)) {
                return proxyServer;
            }
        }

        System.out.println("For some reason proxy server wasn't found out of minigameServerInfoHandler | " + serverName);
        return null;
    }
}
