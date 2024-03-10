package me.fodded.rankedskywars.gameplay.game;

import lombok.Getter;
import me.fodded.rankedskywars.gameplay.game.chest.GameChest;
import me.fodded.rankedskywars.gameplay.game.chest.GameChestType;
import me.fodded.rankedskywars.gameplay.game.states.*;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.map.GameMap;
import me.fodded.spigotcore.gameplay.games.map.GameMapConfig;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

@Getter
public class RankedSkywarsGame extends GameInstance {

    private final Map<GameChestType, Set<Location>> gameChestMap;

    private RankedSkywarsGame(String gameMapPathName) {
        super();

        FileConfiguration config = GameMapConfig.getInstance().getGameInfo();

        setGameMapDisplayName(config.getString(gameMapPathName + ".displayName"));
        setGameMap(new GameMap(config.getString(gameMapPathName + ".world")));

        registerAllGameStates();
        setCurrentGameState(getGameStateList().get(0));

        gameChestMap = GameChest.getGameChestMap(
                getGameMap().getWorld(),
                getGameMap() + ".chests"
        );
    }

    @Override
    public void removePlayer(Player player) {
        getSpectatorPlayersList().remove(player.getUniqueId());
        getAlivePlayersList().remove(player.getUniqueId());
    }

    @Override
    public void addPlayer(Player player) {
        getInitialPlayersList().add(player.getUniqueId());
        getAlivePlayersList().add(player.getUniqueId());
    }

    @Override
    public void initializeGame() {

    }

    @Override
    public void stopGame() {

    }

    private void registerAllGameStates() {
        registerGameState(new GameStateQueuing(this));
        registerGameState(new GameStateStarting(this));
        registerGameState(new GameStateStarted(this));
        registerGameState(new GameStateFirstRefill(this));
        registerGameState(new GameStateSecondRefill(this));
        registerGameState(new GameStateDoom(this));
        registerGameState(new GameStateEnd(this));
    }

    @Override
    public int getMaxPlayers() {
        return 4;
    }

    @Override
    public int getMinPlayers() {
        return 4;
    }
}
