package me.fodded.rankedskywars.gameplay.game;

import lombok.Getter;
import me.fodded.rankedskywars.gameplay.game.chest.GameChest;
import me.fodded.rankedskywars.gameplay.game.chest.GameChestType;
import me.fodded.rankedskywars.gameplay.game.states.*;
import me.fodded.rankedskywars.gameplay.game.tracker.GameDamageTracker;
import me.fodded.rankedskywars.gameplay.game.tracker.GameKillTracker;
import me.fodded.rankedskywars.gameplay.game.tracker.GamePlayerStatisticsTracker;
import me.fodded.rankedskywars.gameplay.tasks.UpdateScoreboardTask;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.map.GameMap;
import me.fodded.spigotcore.gameplay.games.map.GameMapConfig;
import me.fodded.spigotcore.utils.ServerLocations;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class RankedSkywarsGame extends GameInstance {

    private final String gameMapName;

    private final Map<UUID, GamePlayerStatisticsTracker> gamePlayerStatisticsTrackerMap = new HashMap<>();
    private Map<GameChestType, Set<Location>> gameChestMap = new HashMap<>();
    private final Set<Location> gameCagesSet = new HashSet<>();

    private final GameKillTracker gameKillTracker;
    private final GameDamageTracker gameDamageTracker;

    private Location waitingLobbyLocation;

    private RankedSkywarsGame(String gameMapName) {
        super();
        this.gameMapName = gameMapName;

        gameKillTracker = new GameKillTracker(this);
        gameDamageTracker = new GameDamageTracker(this);

        registerAllGameStates();
        initializeGame();
    }

    @Override
    public void initializeGame() {
        FileConfiguration config = GameMapConfig.getInstance().getGameInfo();

        setGameMapDisplayName(config.getString(gameMapName + ".displayName"));
        setGameMap(new GameMap(config.getString(gameMapName + ".world")));

        initializeGameChests();
        initializeWaitingLobbyLocation();
        initializeGameCagesLocations();

        setCurrentGameState(getGameStatesList().get(0));
    }

    @Override
    public void stopGame() {

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
        gamePlayerStatisticsTrackerMap.put(player.getUniqueId(), new GamePlayerStatisticsTracker(this));
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

    public void sendPlayerJoinedMessage(Player player) {
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

    public void sendPlayerLeftMessage(Player player) {
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

    private void initializeGameChests() {
        gameChestMap = GameChest.getGameChestMap(
                getGameMap().getWorld(),
                getGameMap() + ".chests"
        );
    }

    private void initializeWaitingLobbyLocation() {
        FileConfiguration config = GameMapConfig.getInstance().getGameInfo();
        String waitingLobbySerializedLocation = config.getString(getGameMap() + ".waitinglobbylocation");

        waitingLobbyLocation = ServerLocations.deserializeLocation(getGameMap().getWorld(), waitingLobbySerializedLocation);
    }

    private void initializeGameCagesLocations() {
        FileConfiguration config = GameMapConfig.getInstance().getGameInfo();
        List<String> gameCagesSerializedLocations = config.getStringList(getGameMap() + ".cageslocations");

        for(String serializedLocation : gameCagesSerializedLocations) {
            Location deserializeLocation = ServerLocations.deserializeLocation(getGameMap().getWorld(), serializedLocation);
            gameCagesSet.add(deserializeLocation);
        }
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
