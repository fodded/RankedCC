package me.fodded.skywars.gameplay.guis;

import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MinigamesGui extends AbstractGui {

    public MinigamesGui(Player player) {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {

    }
}
