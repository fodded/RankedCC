package me.fodded.spigotcore.gameplay.games;

import lombok.Data;
import me.fodded.spigotcore.gameplay.games.map.GameMap;
import me.fodded.spigotcore.gameplay.games.state.GameState;
import me.fodded.spigotcore.gameplay.games.state.IGame;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public abstract class GameInstance implements IGame {

    private final UUID gameId;

    private int gameGoingTimeSeconds;

    private GameState currentGameState;
    private GameMap gameMap;

    private final List<GameState> gameStateList = new LinkedList<>();
    private String gameMapDisplayName;

    private final List<UUID> initialPlayersList = new LinkedList<>();
    private final List<UUID> alivePlayersList = new LinkedList<>();
    private final List<UUID> spectatorPlayersList = new LinkedList<>();

    public GameInstance() {
        gameId = UUID.randomUUID();
        GameManager.getInstance().initializeNewGame(this);
    }

    public abstract int getMaxPlayers();
    public abstract int getMinPlayers();

    public void registerGameState(GameState gameState) {
        gameStateList.add(gameState);
    }

    public void switchToNextGameState() {
        currentGameState.endGameState();

        GameState nextGameState = getNextGameState(currentGameState);
        if(nextGameState != currentGameState) {
            nextGameState.startGameState();
        }
    }

    public GameState getNextGameState(GameState currentGameState) {
        int currentIndex = gameStateList.indexOf(currentGameState);
        if (currentIndex == -1 || currentIndex == gameStateList.size() - 1) {
            return currentGameState;
        }
        return gameStateList.get(currentIndex + 1);
    }

    public List<GameState> getPreviousGameStates(GameState currentGameState) {
        int currentIndex = gameStateList.indexOf(currentGameState);
        return gameStateList.subList(0, currentIndex);
    }
}
