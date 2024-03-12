package me.fodded.rankedskywars.gameplay.game.tasks;

import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameSpawnDragonTask extends BukkitRunnable {

    private final RankedSkywarsGame gameInstance;
    public GameSpawnDragonTask(RankedSkywarsGame gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public void run() {
        gameInstance.getGameMap().getWorld().spawnEntity(gameInstance.getWaitingLobbyLocation().clone(), EntityType.ENDER_DRAGON);
        for (UUID uuid : gameInstance.getAlivePlayersList()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                player.playSound(player.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
                StringUtils.sendMessage(player, "dragon-spawned");
            }
        }
    }
}
