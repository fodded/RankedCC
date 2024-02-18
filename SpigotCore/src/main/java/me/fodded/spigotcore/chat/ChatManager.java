package me.fodded.spigotcore.chat;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.configs.ConfigLoader;
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

        SpigotCore core = SpigotCore.getInstance();
        core.getPlugin().getServer().getPluginManager().registerEvents(this, core.getPlugin());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        GeneralStats generalStats = generalStatsDataManager.getCachedValue(player.getUniqueId());

        Rank rank = generalStats.getRank();
        if (isPlayerFlooding(event, rank, generalStats)) {
            return;
        }

        String prefix = generalStats.getPrefix().isEmpty() ? rank.getPrefix() : generalStats.getPrefix() + " ";
        String displayedName = generalStats.getDisplayedName().isEmpty() ? player.getName() : generalStats.getDisplayedName();

        String message = getFormattedMessage(event);
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(StringUtils.format(prefix + displayedName + "&f: ") + message);
        }

        floodMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis()+chatDelay);
    }

    private String getFormattedMessage(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if(event.getPlayer().isOp()) {
            message = StringUtils.format(message);
        }
        return message;
    }

    private boolean isPlayerFlooding(AsyncPlayerChatEvent event, Rank rank, GeneralStats generalStats) {
        if(rank.getPriority() > 0) {
            if (floodMap.containsKey(event.getPlayer().getUniqueId())) {
                if (floodMap.get(event.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                    event.getPlayer().sendMessage(StringUtils.format(
                            ConfigLoader.getInstance().getConfig(generalStats.getChosenLanguage() + "-lang.yml").getString("chat-delay")
                    ));
                    return true;
                }
            }
        }
        return false;
    }
}
