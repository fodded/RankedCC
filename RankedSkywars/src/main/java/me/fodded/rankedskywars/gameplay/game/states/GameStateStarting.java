package me.fodded.rankedskywars.gameplay.game.states;

import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.state.GameState;

public class GameStateStarting extends GameState {
    public GameStateStarting(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public int getStateDuration() {
        return 5;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }
}
