package me.fodded.rankedskywars.gameplay.game.states;

import me.fodded.rankedskywars.gameplay.game.chest.action.ChestActionManager;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.state.GameState;

public class GameStateFirstRefill extends GameState {
    public GameStateFirstRefill(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public int getStateDuration() {
        return 80;
    }

    @Override
    public void start() {
        ChestActionManager chestActionManager = new ChestActionManager();
        chestActionManager.refillChests(getGameInstance(), 1);
    }

    @Override
    public void end() {

    }
}