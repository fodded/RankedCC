package me.fodded.spigotcore.gameplay.games;

import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Data
public class GameManager {

    @Getter
    private static final Set<GameInstance> allCurrentGames = new HashSet<>();
    private static GameManager instance;

    public GameManager() {
        instance = this;
    }

    public void initializeNewGame(GameInstance gameInstance) {
        allCurrentGames.add(gameInstance);
    }

    public void removeGameInstance(GameInstance gameInstance) {
        allCurrentGames.remove(gameInstance);
    }

    public static GameManager getInstance() {
        if(instance == null) {
            return new GameManager();
        }
        return instance;
    }
}
