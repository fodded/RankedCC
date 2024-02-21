package me.fodded.skywars.tasks;

import me.fodded.skywars.gameplay.scoreboard.SkywarsLobbyScoreboard;
import me.fodded.spigotcore.gameplay.scoreboard.AbstractScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScoreboardTask extends BukkitRunnable {

    private final Player player;

    public UpdateScoreboardTask(Player player) {
        this.player = player;
        new SkywarsLobbyScoreboard(player.getUniqueId());
    }

    @Override
    public void run() {
        if(player.isOnline()) {
            AbstractScoreboard scoreboardManager = AbstractScoreboard.getScoreboardManager(player);
            scoreboardManager.update();
            return;
        }

        cancel();
    }
}