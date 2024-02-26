package me.fodded.skywars.gameplay.guis;

import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class CosmeticsGui extends AbstractGui {

    public CosmeticsGui(Player player) {
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
        return "Cosmetics Menu";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
    }
}
