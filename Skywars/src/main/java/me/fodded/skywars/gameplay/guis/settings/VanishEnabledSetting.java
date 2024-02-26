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

public class VanishEnabledSetting extends AbstractGuiSetting {

    public VanishEnabledSetting(UUID uniqueId, Rank requiredRank) {
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

        boolean isVanishEnabled = generalStats.isVanished();
        String name = getPlaceholder(config.getString("menus-text.vanish.name"), isVanishEnabled);
        List<String> lore = config.getStringList("menus-text.vanish.description");

        setItemStack(ItemUtils.getItemStack(
                isVanishEnabled ? Material.PAPER : Material.EMPTY_MAP,
                name,
                lore,
                isVanishEnabled
        ));
    }

    private String getPlaceholder(String text, boolean isVanishEnabled) {
        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        String replaceText = config.getString("menus-text.vanish.replace_text");
        return text.replace("%vanish_enabled%", isVanishEnabled ? replaceText.split("%")[0] : replaceText.split("%")[1]);
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        GeneralStatsDataManager.getInstance().applyChangeToRedis(
                getUniqueID(), generalStats -> generalStats.setVanished(!generalStats.isVanished())
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
