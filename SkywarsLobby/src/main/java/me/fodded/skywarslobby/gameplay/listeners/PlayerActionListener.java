package me.fodded.skywarslobby.gameplay.listeners;

import me.fodded.skywarslobby.managers.SkywarsLobbyPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class PlayerActionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Block block = player.getWorld().getBlockAt(player.getLocation().subtract(0,2,0));
        if(!block.getType().equals(Material.AIR)){
            Vector v = player.getLocation().getDirection().multiply(new Vector(2, 1, 2)).multiply(1).setY(1);
            player.setVelocity(v);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() instanceof PlayerInventory) {
            if(player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material material = event.getMaterial();

        SkywarsLobbyPlayer skywarsLobbyPlayer = SkywarsLobbyPlayer.getLobbyPlayer(player.getUniqueId());
        skywarsLobbyPlayer.openGui(material);
    }
}