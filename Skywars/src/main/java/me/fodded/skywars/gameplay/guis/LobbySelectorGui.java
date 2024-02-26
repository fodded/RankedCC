package me.fodded.skywars.gameplay.guis;

import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.servers.SpigotServerManager;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class LobbySelectorGui extends AbstractGui {

    public LobbySelectorGui(Player player) {
        super();

        int counter = 1;
        Inventory inventory = getInventory();

        for(Map.Entry entry : SpigotServerManager.getInstance().getPlayersServerMap("Main-Lobby").entrySet()) {
            String serverName = (String) entry.getKey();
            int playersOnline = (int) entry.getValue();

            boolean thisServer = serverName.equalsIgnoreCase(SpigotCore.getInstance().getServerName());
            ItemStack itemStack = ItemUtils.getItemStack(
                    (thisServer ? Material.RECORD_11 : Material.GOLD_RECORD),
                    (thisServer ? "&c" : "&f") + "Main Lobby &6#" + counter,
                    Arrays.asList(
                            "&fPlayers: " + playersOnline + "/" + Bukkit.getMaxPlayers(),
                            thisServer ? "&cAlready connected" : "&6&lClick to connect!"),
                    thisServer,
                    counter
            );

            inventory.setItem(counter-1, itemStack);
            counter++;
        }
        player.openInventory(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Lobby Selector";
    }
}
