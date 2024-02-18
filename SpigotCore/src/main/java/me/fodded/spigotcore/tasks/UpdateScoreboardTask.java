package me.fodded.spigotcore.tasks;

import me.fodded.spigotcore.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScoreboardTask extends BukkitRunnable {

    private final Player player;

    public UpdateScoreboardTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        ScoreboardManager scoreboardManager = ScoreboardManager.getScoreboardManager(player);
        if(!player.isOnline()) {
            cancel();
            scoreboardManager.removeScoreboard();
            return;
        }

        scoreboardManager.update();
    }

}
