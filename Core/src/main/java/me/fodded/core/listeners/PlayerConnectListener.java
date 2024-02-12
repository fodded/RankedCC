package me.fodded.core.listeners;

import me.fodded.core.Core;
import me.fodded.core.managers.stats.operators.RedisOperations;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RedisOperations.getInstance().publishData(Core.getInstance().getServerName(), Bukkit.getOnlinePlayers().size()+"");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RedisOperations.getInstance().publishData(Core.getInstance().getServerName(), Bukkit.getOnlinePlayers().size()+"");
    }
}
