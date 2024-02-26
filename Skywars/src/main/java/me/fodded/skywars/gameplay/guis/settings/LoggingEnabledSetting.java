package me.fodded.skywars.gameplay.guis.settings;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.skywars.Main;
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

public class LoggingEnabledSetting extends AbstractGuiSetting {

    public LoggingEnabledSetting(UUID uniqueId, Rank requiredRank) {
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

        boolean isLoggingEnabled = generalStats.isLogging();
        String name = getPlaceholder(config.getString("menus-text.logs.name"), isLoggingEnabled);
        List<String> lore = config.getStringList("menus-text.logs.description");

        setItemStack(ItemUtils.getItemStack(
                isLoggingEnabled ? Material.PAPER : Material.EMPTY_MAP,
                name,
                lore,
                isLoggingEnabled
        ));
    }

    private String getPlaceholder(String text, boolean isLoggingEnabled) {
        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        String replaceText = config.getString("menus-text.logs.replace_text");
        return text.replace("%logs_enabled%", isLoggingEnabled ? replaceText.split("%")[0] : replaceText.split("%")[1]);
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        GeneralStatsDataManager.getInstance().applyChangeToRedis(
                getUniqueID(), generalStats -> generalStats.setLogging(!generalStats.isLogging())
        );

        player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if(player == null || !player.isOnline()) {
                return;
            }

            new SettingsGui(player);
        }, 2L);
    }
}
