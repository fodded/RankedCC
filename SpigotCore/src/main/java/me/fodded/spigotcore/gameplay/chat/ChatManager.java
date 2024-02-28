package me.fodded.spigotcore.gameplay.chat;

import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager implements Listener {

    private final Map<UUID, Long> floodMap = new HashMap<>();
    private final long chatDelay;

    public ChatManager(long chatDelay) {
        this.chatDelay = chatDelay;

        SpigotCore spigotCore = SpigotCore.getInstance();
        spigotCore.getPlugin().getServer().getPluginManager().registerEvents(this, spigotCore.getPlugin());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());
        if (!generalStats.isChatEnabled()) {
            sendMessage(player, "no-chat-allowed");
            return;
        }

        if (isPlayerFlooding(player)) {
            sendMessage(player, "chat-delay");
            return;
        }

        String prefix = getPrefix(generalStats);
        String displayedName = player.getName();
        String message = getFormattedMessage(event);

        broadcastMessage(prefix, displayedName, message);
        floodMap.put(player.getUniqueId(), System.currentTimeMillis() + chatDelay);
    }

    private void sendMessage(Player player, String messageKey) {
        String message = StringUtils.format(LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getString(messageKey));
        player.sendMessage(message);
    }

    private boolean isPlayerFlooding(Player player) {
        if (player.isOp()) {
            return false;
        }
        Long nextAllowedChatTime = floodMap.get(player.getUniqueId());
        return nextAllowedChatTime != null && nextAllowedChatTime > System.currentTimeMillis();
    }

    private String getPrefix(GeneralStats generalStats) {
        if (!generalStats.getPrefix().isEmpty()) {
            return generalStats.getPrefix() + " ";
        }
        if (!generalStats.getDisguisedName().isEmpty()) {
            return generalStats.getDisguisedRank().getPrefix();
        }
        return generalStats.getRank().getPrefix();
    }

    private String getFormattedMessage(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (event.getPlayer().isOp()) {
            message = StringUtils.format(message);
        }
        return message;
    }

    private void broadcastMessage(String prefix, String displayedName, String message) {
        String formattedMessage = StringUtils.format(prefix + displayedName + "&f: ") + message;
        for (Player eachPlayer : Bukkit.getOnlinePlayers()) {
            GeneralStats eachPlayerGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(eachPlayer.getUniqueId());
            if (eachPlayerGeneralStats.isChatEnabled()) {
                eachPlayer.sendMessage(formattedMessage);
            }
        }
    }
}
