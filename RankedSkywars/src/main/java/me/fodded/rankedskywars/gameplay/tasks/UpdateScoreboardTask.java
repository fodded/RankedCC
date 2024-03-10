package me.fodded.rankedskywars.gameplay.tasks;

import me.fodded.rankedskywars.gameplay.scoreboard.RankedSkywarsScoreboard;
import me.fodded.spigotcore.gameplay.scoreboard.AbstractScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScoreboardTask extends BukkitRunnable {

    private final Player player;

    public UpdateScoreboardTask(Player player) {
        this.player = player;
        new RankedSkywarsScoreboard(player.getUniqueId());
    }

    @Override
    public void run() {
        if(player.isOnline()) {
            AbstractScoreboard scoreboardManager = AbstractScoreboard.getScoreboardManager(player.getUniqueId());
            scoreboardManager.update();
            return;
        }

        cancel();
    }
}