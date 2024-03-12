package me.fodded.rankedskywars.gameplay.game.states;

import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.state.GameState;
import org.bukkit.Location;
import org.bukkit.Material;

public class GameStateQueuing extends GameState {
    public GameStateQueuing(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public int getStateDuration() {
        return -1;
    }

    @Override
    public void start() {
        RankedSkywarsGame game = (RankedSkywarsGame) getGameInstance();

        for(Location location : game.getGameCagesSet()) {
            spawnGameCage(location, false);
        }
    }

    public void spawnGameCage(Location location, boolean remove) {
        Material block = remove ? Material.AIR : Material.GLASS;
        int yTimes = remove ? 5 : 4;

        for (int y = 0; y <= yTimes; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (y > 0 && y < 4 && !remove && x == 0 && z == 0) {
                        continue;
                    }
                    location.clone().add(x, y-1, z).getBlock().setType(block);
                }
            }
        }
    }

    @Override
    public void end() {

    }
}
