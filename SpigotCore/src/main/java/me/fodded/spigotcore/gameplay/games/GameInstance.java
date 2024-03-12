package me.fodded.spigotcore.gameplay.games;

import lombok.Data;
import me.fodded.spigotcore.gameplay.games.map.GameMap;
import me.fodded.spigotcore.gameplay.games.state.GameState;
import me.fodded.spigotcore.gameplay.games.state.IGame;

import java.util.*;

@Data
public abstract class GameInstance implements IGame {

    private final UUID gameId;

    private int gameGoingTimeSeconds;

    private GameState currentGameState;
    private GameMap gameMap;

    private final List<GameState> gameStatesList = new ArrayList<>();
    private String gameMapDisplayName;

    private final Set<UUID> initialPlayersList = new HashSet<>();
    private final Set<UUID> alivePlayersList = new HashSet<>();
    private final Set<UUID> spectatorPlayersList = new HashSet<>();

    public GameInstance() {
        gameId = UUID.randomUUID();
        GameManager.getInstance().initializeNewGame(this);
    }

    public abstract int getMaxPlayers();
    public abstract int getMinPlayers();

    public void registerGameState(GameState gameState) {
        gameStatesList.add(gameState);
    }

    public void switchToNextGameState() {
        currentGameState.endGameState();

        GameState nextGameState = getNextGameState(currentGameState);
        if(nextGameState != currentGameState) {
            nextGameState.startGameState();
        }
    }

    public GameState getNextGameState(GameState currentGameState) {
        int currentIndex = gameStatesList.indexOf(currentGameState);
        if (currentIndex == -1 || currentIndex == gameStatesList.size() - 1) {
            return currentGameState;
        }
        return gameStatesList.get(currentIndex + 1);
    }

    public List<GameState> getPreviousGameStates(GameState currentGameState) {
        int currentIndex = gameStatesList.indexOf(currentGameState);
        return gameStatesList.subList(0, currentIndex);
    }

    public boolean isPlayerAlive(UUID playerUniqueId) {
        return alivePlayersList.contains(playerUniqueId);
    }

    public boolean isPlayerSpectator(UUID playerUniqueId) {
        return spectatorPlayersList.contains(playerUniqueId);
    }

    public boolean isPlayerPresentInGame(UUID playerUniqueId) {
        return alivePlayersList.contains(playerUniqueId) || spectatorPlayersList.contains(playerUniqueId);
    }
}
