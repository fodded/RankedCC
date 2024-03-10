package me.fodded.rankedskywars.gameplay.guis;

import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class KitSelectorGui extends AbstractGui {

    public KitSelectorGui(Player player) {
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
        return "Kit Selector";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
    }
}