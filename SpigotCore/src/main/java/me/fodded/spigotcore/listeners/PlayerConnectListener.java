package me.fodded.spigotcore.listeners;

import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.model.DataManager;
import me.fodded.core.model.GlobalDataManager;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID uniqueId = event.getUniqueId();

        //GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        //generalStatsDataManager.getCachedValue(uniqueId);
    }

    @EventHandler
    public void onPlayerJoinSync(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Bukkit.getScheduler().runTaskLater(SpigotCore.getInstance().getPlugin(), () -> {
            Player player = event.getPlayer();
            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());

            if(!generalStats.getDisguisedName().isEmpty()) {
                DisguiseManager.getInstance().setDisguise(
                        player,
                        generalStats.getDisguisedName(),
                        generalStats.getDisguisedSkinTexture(),
                        generalStats.getDisguisedSkinSignature()
                );
            }
        }, 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        event.setQuitMessage(null);
        for(GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
            dataManager.removeFromCache(uniqueId);
        }
    }
}
