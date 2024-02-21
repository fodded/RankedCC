package me.fodded.skywars.gameplay.guis.settings;

import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.gui.AbstractGuiSetting;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
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
        List<String> languagesList = ConfigLoader.getInstance().getLanguagesList();
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
                "&fCurrently Selected &6&lLanguage",
                loreList
        ));
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID());
        String newLanguage = getNextLanguage(generalStats.getChosenLanguage());
        if(event.isRightClick()) {
            newLanguage = getPreviousLanguage(generalStats.getChosenLanguage());
        }

        String finalNewLanguage = newLanguage;
        GeneralStatsDataManager.getInstance().applyChange(
                getUniqueID(), generalStats1 -> generalStats1.setChosenLanguage(finalNewLanguage)
        );

        player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
        new SettingsGui(player);
    }

    private int getIndex(String language) {
        List<String> languagesList = ConfigLoader.getInstance().getLanguagesList();
        for(int i = 0; i < languagesList.size(); i++) {
            if(languagesList.get(i).equalsIgnoreCase(language)) {
                return i;
            }
        }
        return 0;
    }

    private String getNextLanguage(String currentLanguage) {
        List<String> languagesList = ConfigLoader.getInstance().getLanguagesList();
        int currentLanguageIndex = getIndex(currentLanguage);

        if(languagesList.size()-1<currentLanguageIndex+1) {
            return languagesList.get(0);
        }
        return languagesList.get(currentLanguageIndex+1);
    }

    private String getPreviousLanguage(String currentLanguage) {
        List<String> languagesList = ConfigLoader.getInstance().getLanguagesList();
        int currentLanguageIndex = getIndex(currentLanguage);

        // english 0
        // 3
        if(currentLanguageIndex-1 < 0) {
            return languagesList.get(languagesList.size()-1);
        }
        return languagesList.get(currentLanguageIndex-1);
    }
}
