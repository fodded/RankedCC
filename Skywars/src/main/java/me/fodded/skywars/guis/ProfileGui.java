package me.fodded.skywars.guis;

import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class ProfileGui extends AbstractGui {

    public ProfileGui(Player player) {
        super();
        setItem(ItemUtils.getItemStack(
                Material.BARRIER,
                "&cClose",
                Arrays.asList("&7Closing the menu")
        ), 5);
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        player.sendMessage(StringUtils.format("&cYou clicked in the inventory"));
        event.setCancelled(true);
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Profile Menu";
    }
}
