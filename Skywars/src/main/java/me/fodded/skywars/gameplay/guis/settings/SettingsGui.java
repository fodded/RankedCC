package me.fodded.skywars.gameplay.guis.settings;

import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SettingsGui extends AbstractGui {

    public SettingsGui(Player player) {
        super();
        addSetting(0, new FriendRequestSetting(player.getUniqueId()));
        addSetting(1, new ChatEnabledSetting(player.getUniqueId()));

        addSetting(7, new LanguageSelectorSetting(player.getUniqueId()));
        initializeGuiSettings();

        player.openInventory(getInventory());
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
        return "Settings Menu";
    }
}
