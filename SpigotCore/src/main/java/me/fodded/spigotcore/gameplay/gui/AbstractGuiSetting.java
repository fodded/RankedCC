package me.fodded.spigotcore.gameplay.gui;

import lombok.Getter;
import lombok.Setter;
import me.fodded.core.managers.ranks.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public abstract class AbstractGuiSetting implements Listener {

    private final UUID uniqueID;
    private final Rank requiredRank;

    @Setter
    private ItemStack itemStack;

    public AbstractGuiSetting(UUID uniqueID, Rank requiredRank) {
        this.uniqueID = uniqueID;
        this.requiredRank = requiredRank;
    }

    public abstract void initializeItemStack();
    public abstract void onClick(InventoryClickEvent event, Player player);
}
