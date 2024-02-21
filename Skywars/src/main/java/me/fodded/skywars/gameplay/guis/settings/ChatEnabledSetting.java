package me.fodded.skywars.gameplay.guis.settings;

import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.gameplay.gui.AbstractGuiSetting;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.UUID;

public class ChatEnabledSetting extends AbstractGuiSetting {

    public ChatEnabledSetting(UUID uniqueId) {
        super(uniqueId);
        initializeItemStack();
    }

    @Override
    public void initializeItemStack() {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID());
        boolean flag = generalStats.isChatEnabled();

        setItemStack(ItemUtils.getItemStack(
                flag ? Material.PAPER : Material.EMPTY_MAP,
                "&fChat is &6&l" + (flag ? "Enabled" : "Disabled"),
                Arrays.asList(
                        "&fDisables/Enabled the chat function",
                        "&fRight Click to turn &6on/off&f this option."
                ),
                generalStats.isChatEnabled()
        ));
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueID());
        GeneralStatsDataManager.getInstance().applyChange(
                getUniqueID(), generalStats1 -> generalStats1.setChatEnabled(!generalStats.isChatEnabled())
        );

        player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
        new SettingsGui(player);
    }
}
