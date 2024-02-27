package me.fodded.bungeecord.serverhandlers.servers;

import lombok.Getter;
import me.fodded.bungeecord.serverhandlers.AbstractServerInfoHandler;
import me.fodded.bungeecord.serverhandlers.BungeeServerHandler;
import me.fodded.core.Core;
import me.fodded.core.model.Redis;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

@Getter
public class MinigameServerInfoHandler extends AbstractServerInfoHandler {

    @Getter
    private static final List<MinigameServerInfoHandler> minigameServerInfoHandlers = new LinkedList<>();

    private final int playersQueuing;

    public MinigameServerInfoHandler(String serverName, int playersQueuing, int playersInTotal) {
        super(serverName, playersInTotal);
        this.playersQueuing = playersQueuing;

        minigameServerInfoHandlers.add(this);
    }

    public ServerInfo getSuitableServer(String serverCurrentlyOn) {
        // We find a server with the least amount of players in total and most amount of players queuing
        Collections.sort(minigameServerInfoHandlers, Comparator
                .comparingInt(MinigameServerInfoHandler::getPlayersQueuing).reversed()
                .thenComparing(MinigameServerInfoHandler::getPlayersOnline));

        // Here we get a server which is not running a game right now and has the most players on atm
        for(MinigameServerInfoHandler serverInfoHandler : minigameServerInfoHandlers) {
            // We check if the server we are seeking for is not the same we are currently on
            if(serverInfoHandler.getServerName().equalsIgnoreCase(serverCurrentlyOn)) continue;
            return getProxyServer(serverInfoHandler.getServerName());
        }

        // In case something went wrong we are going to give the first available server from the list
        return getProxyServer(minigameServerInfoHandlers.get(0).getServerName());
    }

    public void clearAndUpdateServerInformation() {
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
}
