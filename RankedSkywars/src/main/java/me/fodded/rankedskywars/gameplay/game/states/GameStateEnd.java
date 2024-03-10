package me.fodded.rankedskywars.gameplay.game.states;

import me.fodded.spigotcore.gameplay.games.GameInstance;
import me.fodded.spigotcore.gameplay.games.state.GameState;

public class GameStateEnd extends GameState {
    public GameStateEnd(GameInstance gameInstance) {
        super(gameInstance);
    }

    @Override
    public int getStateDuration() {
        return -1;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }
}