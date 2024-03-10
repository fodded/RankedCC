package me.fodded.rankedskywars.gameplay.game.states;

import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.state.GameState;

public class GameStateDoom extends GameState {
    public GameStateDoom(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public int getStateDuration() {
        return 90;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }
}