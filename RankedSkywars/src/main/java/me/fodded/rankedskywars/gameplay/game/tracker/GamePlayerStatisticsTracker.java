package me.fodded.rankedskywars.gameplay.game.tracker;

import lombok.Data;
import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class GamePlayerStatisticsTracker {

    private final RankedSkywarsGame gameInstance;

    private final Map<UUID, Double> damagedPlayersMap = new HashMap<>();
    private int kills, assists;

    private UUID lastAttackFromPlayer;

    public GamePlayerStatisticsTracker(RankedSkywarsGame gameInstance) {
        this.gameInstance = gameInstance;
    }
}
