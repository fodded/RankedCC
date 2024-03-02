package me.fodded.skywarslobby.gameplay.guis;

import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class MinigamesGui extends AbstractGui {

    public MinigamesGui(Player player) {
        setItem(
                ItemUtils.getItemStack(Material.BARRIER, "&cNothing yet", Arrays.asList("&f")),
                0
        );
        player.openInventory(getInventory());
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Minigames Menus";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        switch (event.getSlot()) {
            case 0:
                sendToSkywarsLobby(player);
                break;
        }

        event.setCancelled(true);
    }

    private void sendToSkywarsLobby(Player player) {
        // TODO: put this code in the bungeecord side so we can call it with redis
    }
}
