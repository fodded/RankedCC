package me.fodded.spigotcore.gameplay.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public abstract class AbstractGuiSetting {

    private final UUID uniqueID;
    @Setter
    private ItemStack itemStack;

    public AbstractGuiSetting(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public abstract void initializeItemStack();
    public abstract void onClick(InventoryClickEvent event, Player player);
}
