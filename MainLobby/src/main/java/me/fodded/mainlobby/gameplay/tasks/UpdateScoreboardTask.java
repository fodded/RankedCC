package me.fodded.mainlobby.gameplay.tasks;

import me.fodded.mainlobby.gameplay.scoreboard.MainLobbyScoreboard;
import me.fodded.spigotcore.gameplay.scoreboard.AbstractScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScoreboardTask extends BukkitRunnable {

    private final Player player;

    public UpdateScoreboardTask(Player player) {
        this.player = player;
        new MainLobbyScoreboard(player.getUniqueId());
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