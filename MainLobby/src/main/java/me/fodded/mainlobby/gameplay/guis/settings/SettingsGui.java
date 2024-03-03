package me.fodded.mainlobby.gameplay.guis.settings;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SettingsGui extends AbstractGui {

    public SettingsGui(Player player) {
        super();
        addSetting(0, new FriendRequestSetting(player.getUniqueId(), Rank.DEFAULT));
        addSetting(1, new ChatEnabledSetting(player.getUniqueId(), Rank.DEFAULT));
        addSetting(2, new PrivateMessagesSetting(player.getUniqueId(), Rank.DEFAULT));

        addSetting(3, new LoggingEnabledSetting(player.getUniqueId(), Rank.MODERATOR));
        addSetting(4, new VanishEnabledSetting(player.getUniqueId(), Rank.YOUTUBE));

        addSetting(7, new LanguageSelectorSetting(player.getUniqueId(), Rank.DEFAULT));
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
