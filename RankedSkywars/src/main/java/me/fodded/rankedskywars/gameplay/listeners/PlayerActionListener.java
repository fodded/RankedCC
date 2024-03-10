package me.fodded.rankedskywars.gameplay.listeners;

import me.fodded.rankedskywars.managers.RankedSkywarsPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerActionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material material = event.getMaterial();

        RankedSkywarsPlayer mainLobbyPlayer = RankedSkywarsPlayer.getLobbyPlayer(player.getUniqueId());
        mainLobbyPlayer.openGui(material);
    }
}