package me.fodded.mainlobby.gameplay.guis.settings;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.mainlobby.Main;
import me.fodded.spigotcore.gameplay.gui.AbstractGuiSetting;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PrivateMessagesSetting extends AbstractGuiSetting {

    public PrivateMessagesSetting(UUID uniqueId, Rank requiredRank) {
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

        boolean areMessagesEnabled = generalStats.isPrivateMessagesEnabled();
        String name = config.getString("menus-text.messages.name");
        List<String> lore = getPlaceholder(config.getStringList("menus-text.messages.description"), areMessagesEnabled);

        setItemStack(ItemUtils.getItemStack(
                areMessagesEnabled ? Material.PAPER : Material.EMPTY_MAP,
                name,
                lore,
                areMessagesEnabled
        ));
    }

    private List<String> getPlaceholder(List<String> lore, boolean areMessagesEnabled) {
        List<String> listToReturn = new LinkedList<>();

        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        String replaceText = config.getString("menus-text.messages.replace_text");

        for(String text : lore) {
            listToReturn.add(
                    text.replace("%private_messages%", areMessagesEnabled ? replaceText.split("%")[0] : replaceText.split("%")[1])
            );
        }
        return listToReturn;
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        GeneralStatsDataManager.getInstance().applyChangeToRedis(
                getUniqueID(), generalStats -> generalStats.setPrivateMessagesEnabled(!generalStats.isPrivateMessagesEnabled())
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
