package me.fodded.skywarslobby.gameplay.guis.settings;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.skywarslobby.Main;
import me.fodded.skywarslobby.managers.SkywarsLobbyPlayer;
import me.fodded.spigotcore.gameplay.gui.AbstractGuiSetting;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LanguageSelectorSetting extends AbstractGuiSetting {

    public LanguageSelectorSetting(UUID uniqueId, Rank rank) {
        super(uniqueId, rank);
        if(!Rank.hasPermission(getRequiredRank(), getUniqueID())) {
            return;
        }

        initializeItemStack();
    }

    @Override
    public void initializeItemStack() {
        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID());

        String name = config.getString("menus-text.languages.name");
        List<String> lore = getPlaceholder(config.getStringList("menus-text.languages.description"), generalStats);

        setItemStack(ItemUtils.getItemStack(
                Material.BOOK,
                name,
                lore
        ));
    }

    private List<String> getPlaceholder(List<String> lore, GeneralStats generalStats) {
        List<String> listToReturn = new LinkedList<>();

        Configuration config = LanguageManager.getInstance().getLanguageConfig(getUniqueID());
        String selectedLanguageSymbol = config.getString("menus-text.languages.selected_language");

        for(String text : lore) {
            if(text.contains("%languages%")) {
                addLanguages(generalStats, listToReturn, selectedLanguageSymbol);
            } else {
                listToReturn.add(text);
            }
        }
        return listToReturn;
    }

    private void addLanguages(GeneralStats generalStats, List<String> listToReturn, String selectedLanguageSymbol) {
        List<String> languagesList = LanguageManager.getInstance().getLangaugesList();
        for(String language : languagesList) {
            boolean isSelectedLanguage = generalStats.getChosenLanguage().equalsIgnoreCase(language);
            if(isSelectedLanguage) {
                listToReturn.add(StringUtils.format(selectedLanguageSymbol + " &f" + language));
                continue;
            }
            listToReturn.add(StringUtils.format("&f" + language));
        }
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

            SkywarsLobbyPlayer lobbyPlayer = SkywarsLobbyPlayer.getLobbyPlayer(player.getUniqueId());
            lobbyPlayer.setItemsToPlayerInventory();
            new SettingsGui(player);
        }, 3L);
    }
}
