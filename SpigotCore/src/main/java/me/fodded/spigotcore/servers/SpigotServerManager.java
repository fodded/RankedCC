package me.fodded.spigotcore.servers;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.spigotcore.SpigotCore;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.Map;

public class SpigotServerManager {

    @Getter
    private static SpigotServerManager instance;

    private final RMap<String, Integer> playersMap;

    public SpigotServerManager() {
        instance = this;
        playersMap = Core.getInstance().getRedis().getRedissonClient().getMap("playersMap");
    }

    public void updatePlayerCount(int playersAmount) {
        playersMap.put(
                SpigotCore.getInstance().getServerName(),
                playersAmount
        );
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

    public Map<String, Integer> getPlayersServerMap(String pattern) {
        Map<String, Integer> playersServerMap = new HashMap<>();
        for(Map.Entry entry : playersMap.entrySet()) {
            String serverName = (String) entry.getKey();
            int playersOnTheServer = (int) entry.getValue();

            if(serverName.contains(pattern)) {
                playersServerMap.put(serverName, playersOnTheServer);
            }
        }
        return playersServerMap;
    }
}
