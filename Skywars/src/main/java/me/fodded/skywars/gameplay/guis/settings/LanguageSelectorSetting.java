package me.fodded.skywars.gameplay.guis.settings;

import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.skywars.Main;
import me.fodded.skywars.managers.LobbyPlayer;
import me.fodded.spigotcore.gameplay.gui.AbstractGuiSetting;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LanguageSelectorSetting extends AbstractGuiSetting {

    public LanguageSelectorSetting(UUID uniqueId) {
        super(uniqueId);
        initializeItemStack();
    }

    @Override
    public void initializeItemStack() {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID());

        List<String> loreList = new LinkedList<>();
        loreList.add(StringUtils.format("&fThis function translates messages in the language you choose"));
        loreList.add(StringUtils.format("&f"));

        // 1 - english 2 - russian
        List<String> languagesList = LanguageManager.getInstance().getLangaugesList();
        for(String language : languagesList) {
            boolean isSelectedLanguage = generalStats.getChosenLanguage().equalsIgnoreCase(language);

            if(isSelectedLanguage) {
                loreList.add(StringUtils.format("&6-> &f" + language));
                continue;
            }
            loreList.add(StringUtils.format("&f" + language));
        }

        loreList.add(StringUtils.format("&7"));
        loreList.add(StringUtils.format("&6Left Click &fto go to the next language"));
        loreList.add(StringUtils.format("&6Right Click &fto go to the previous language"));

        setItemStack(ItemUtils.getItemStack(
                Material.BOOK,
                "&fCurrent Selected &6&lLanguage",
                loreList
        ));
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        String currentSelectedLanguage = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID()).getChosenLanguage();
        LanguageManager languageManager = LanguageManager.getInstance();

        String newLanguage;
        if(event.isRightClick()) {
            newLanguage = languageManager.getPreviousLanguage(currentSelectedLanguage);
        } else {
            newLanguage = languageManager.getNextLanguage(currentSelectedLanguage);
        }

        GeneralStatsDataManager.getInstance().applyChange(
                getUniqueID(), generalStats -> generalStats.setChosenLanguage(newLanguage)
        );
        player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);

        // We run the open menu later because otherwise we get visual bug like nothing's been changed
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if(player == null || !player.isOnline()) {
                return;
            }

            LobbyPlayer lobbyPlayer = LobbyPlayer.getLobbyPlayer(player.getUniqueId());
            lobbyPlayer.setItemsToPlayerInventory();
            new SettingsGui(player);
        }, 2L);
    }
}
