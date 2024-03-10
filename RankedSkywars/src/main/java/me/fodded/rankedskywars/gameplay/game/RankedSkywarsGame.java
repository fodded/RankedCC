package me.fodded.rankedskywars.gameplay.game;

import lombok.Getter;
import me.fodded.rankedskywars.gameplay.game.chest.GameChest;
import me.fodded.rankedskywars.gameplay.game.chest.GameChestType;
import me.fodded.rankedskywars.gameplay.game.states.*;
import me.fodded.rankedskywars.gameplay.tasks.UpdateScoreboardTask;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.map.GameMap;
import me.fodded.spigotcore.gameplay.games.map.GameMapConfig;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class RankedSkywarsGame extends GameInstance {

    private final Map<GameChestType, Set<Location>> gameChestMap;
    private Location lobbyLocation;

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

        UpdateScoreboardTask updateScoreboardTask = new UpdateScoreboardTask(player);
        updateScoreboardTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 0, 20L);

        sendPlayerJoinedMessage(player);
    }

    private void sendPlayerJoinedMessage(Player player) {
        for(UUID uuid : getAlivePlayersList()) {
            Player eachPlayer = Bukkit.getPlayer(uuid);
            if(eachPlayer != null) {
                String message = StringUtils.getMessage(eachPlayer, "player-joined")
                        .replace("%player%", player.getDisplayName())
                        .replace("%players%", getAlivePlayersList().size()+"");

                StringUtils.sendMessage(eachPlayer, message);
            }
        }
    }

    private void sendPlayerLeftMessage(Player player) {
        for(UUID uuid : getAlivePlayersList()) {
            Player eachPlayer = Bukkit.getPlayer(uuid);
            if(eachPlayer != null) {
                String message = StringUtils.getMessage(eachPlayer, "player-left")
                        .replace("%player%", player.getDisplayName())
                        .replace("%players%", getAlivePlayersList().size()+"");;
                StringUtils.sendMessage(eachPlayer, message);
            }
        }
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
