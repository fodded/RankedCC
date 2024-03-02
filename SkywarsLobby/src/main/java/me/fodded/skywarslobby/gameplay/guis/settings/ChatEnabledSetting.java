package me.fodded.skywarslobby.gameplay.guis.settings;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.skywarslobby.Main;
import me.fodded.spigotcore.gameplay.gui.AbstractGuiSetting;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.UUID;

public class ChatEnabledSetting extends AbstractGuiSetting {

    public ChatEnabledSetting(UUID uniqueId, Rank requiredRank) {
        super(uniqueId, requiredRank);
        if(!Rank.hasPermission(getRequiredRank(), getUniqueID())) {
            return;
        }

        initializeItemStack();
    }

    @Override
    public void initializeItemStack() {
        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID());

        boolean isChatEnabled = generalStats.isChatEnabled();
        String name = getPlaceholder(config.getString("menus-text.chat.name"), isChatEnabled);
        List<String> lore = config.getStringList("menus-text.chat.description");

        setItemStack(ItemUtils.getItemStack(
                isChatEnabled ? Material.PAPER : Material.EMPTY_MAP,
                name,
                lore,
                isChatEnabled
        ));
    }

    private String getPlaceholder(String text, boolean isChatEnabled) {
        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        String replaceText = config.getString("menus-text.chat.replace_text");
        return text.replace("%chat_enabled%", isChatEnabled ? replaceText.split("%")[0] : replaceText.split("%")[1]);
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        GeneralStatsDataManager.getInstance().applyChangeToRedis(
                getUniqueID(), generalStats -> generalStats.setChatEnabled(!generalStats.isChatEnabled())
        );

        player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if(player == null || !player.isOnline()) {
                return;
            }

            new SettingsGui(player);
        }, 3L);
    }
}
