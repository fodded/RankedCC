package me.fodded.rankedskywars.gameplay.game.tasks;

import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.spigotcore.gameplay.games.state.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class GameGoingTask extends BukkitRunnable {

    private final RankedSkywarsGame gameInstance;
    public GameGoingTask(RankedSkywarsGame gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public void run() {
        gameInstance.setGameGoingTimeSeconds(gameInstance.getGameGoingTimeSeconds()+1);

        int neededTimeForNextState = getNeededTimeForNextState();
        if(gameInstance.getGameGoingTimeSeconds() >= neededTimeForNextState) {
            gameInstance.switchToNextGameState();
        }
    }

    private int getNeededTimeForNextState() {
        GameState gameState = gameInstance.getCurrentGameState();
        return gameInstance.getPreviousGameStates(gameState).stream()
                .filter(previousGameStates -> previousGameStates.getStateDuration() >= 0)
                .mapToInt(GameState::getStateDuration)
                .sum() + gameState.getStateDuration();
    }
}