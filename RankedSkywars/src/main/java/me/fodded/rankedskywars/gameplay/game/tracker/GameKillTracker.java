package me.fodded.rankedskywars.gameplay.game.tracker;

import lombok.Getter;
import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GameKillTracker {

    private final RankedSkywarsGame gameInstance;
    private final Map<UUID, UUID> killedPlayersByPlayersMap = new HashMap<>();

    public GameKillTracker(RankedSkywarsGame gameInstance) {
        this.gameInstance = gameInstance;
    }
}
