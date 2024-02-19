package me.fodded.spigotcore.gameplay.gui;

import me.fodded.spigotcore.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractGui implements Listener, InventoryHolder {

    //private static Map<UUID, AbstractGui> openedGuisMap = new HashMap<>();
    private final Inventory inventory;

    public AbstractGui() {
        this.inventory = Bukkit.createInventory(this, getSize(), getTitle());

        JavaPlugin plugin = SpigotCore.getInstance().getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setItem(ItemStack itemStack, int slot) {
        inventory.setItem(slot, itemStack);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(inventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        onClick(event, player);
    }

    public abstract int getSize();
    public abstract String getTitle();
    public abstract void onClick(InventoryClickEvent event, Player player);

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
