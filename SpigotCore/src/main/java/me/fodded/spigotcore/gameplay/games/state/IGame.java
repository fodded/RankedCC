package me.fodded.spigotcore.gameplay.games.state;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IGame {

    UUID getGameId();

    void removePlayer(Player player);
    void addPlayer(Player player);

    void initializeGame();
    void stopGame();
}
