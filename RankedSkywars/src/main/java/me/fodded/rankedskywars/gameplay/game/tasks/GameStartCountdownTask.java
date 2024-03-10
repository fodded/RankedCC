package me.fodded.rankedskywars.gameplay.game.tasks;

import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameStartCountdownTask extends BukkitRunnable {

    private final RankedSkywarsGame gameInstance;
    private int timeLeft;
    public GameStartCountdownTask(RankedSkywarsGame gameInstance, int timeLeft) {
        this.gameInstance = gameInstance;
        this.timeLeft = timeLeft;
    }

    @Override
    public void run() {
        if(timeLeft <= 0) {
            gameInstance.switchToNextGameState();
            cancel();
            return;
        }

        for (UUID uuid : gameInstance.getAlivePlayersList()) {
            Player eachPlayer = Bukkit.getPlayer(uuid);
            if(eachPlayer != null) {
                String message = StringUtils
                        .getMessage(eachPlayer, "game-starting-message")
                        .replace("%count%", timeLeft+"");

                eachPlayer.sendMessage(StringUtils.format(message));
                eachPlayer.playSound(eachPlayer.getLocation(), Sound.NOTE_PLING, 0.3f, 2.0f);
            }
        }
        timeLeft--;
    }
}