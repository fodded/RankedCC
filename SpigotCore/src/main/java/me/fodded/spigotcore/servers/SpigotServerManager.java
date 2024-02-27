package me.fodded.spigotcore.servers;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.servers.tasks.ServerPlayersAmountTask;
import org.redisson.api.RMap;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class SpigotServerManager {

    @Getter
    private static SpigotServerManager instance;

    private final RMap<String, Integer> playersMap;

    public SpigotServerManager() {
        instance = this;
        playersMap = Core.getInstance().getRedis().getRedissonClient().getMap("playersMap");

        ServerPlayersAmountTask serverPlayersAmountTask = new ServerPlayersAmountTask();
        serverPlayersAmountTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 20L, 20L);
    }

    public void updatePlayerCount(int playersAmount) {
        CompletableFuture.runAsync(() -> {
            playersMap.put(
                    SpigotCore.getInstance().getServerName(),
                    playersAmount
            );
        });
    }

    public Integer getAmountOfPlayers(String pattern) {
        int onlinePlayers = 0;
        for(Map.Entry entry : playersMap.entrySet()) {
            String serverName = (String) entry.getKey();
            int playersOnTheServer = (int) entry.getValue();

            if(serverName.contains(pattern)) {
                onlinePlayers += playersOnTheServer;
            }
        }
        return onlinePlayers;
    }

    // Server Index : Server Online
    public TreeMap<Integer, Integer> getPlayersServerMap(String pattern) {
        TreeMap<Integer, Integer> playersServerMap = new TreeMap<>();
        for(Map.Entry entry : playersMap.entrySet()) {
            String serverName = (String) entry.getKey();
            if(!serverName.contains(pattern)) {
                continue;
            }

            int playersOnTheServer = (int) entry.getValue();
            int serverIndex = Integer.parseInt(serverName.substring(pattern.length()+1));

            playersServerMap.put(serverIndex, playersOnTheServer);
        }
        return playersServerMap;
    }
}
