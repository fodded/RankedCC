package me.fodded.rankedskywars.gameplay.game.states;

import me.fodded.rankedskywars.gameplay.game.chest.action.ChestActionManager;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.state.GameState;

public class GameStateStarted extends GameState {
    public GameStateStarted(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public int getStateDuration() {
        return 145;
    }

    @Override
    public void start() {
        ChestActionManager chestActionManager = new ChestActionManager();
        chestActionManager.fillChests(getGameInstance());
    }

    @Override
    public void end() {

    }
}
