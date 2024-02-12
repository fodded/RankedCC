package me.fodded.core.managers.chat;

import me.fodded.core.Core;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.ranks.RankManager;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager implements Listener {

    private Map<UUID, Long> floodMap = new HashMap<>();

    private long chatDelay;
    public ChatManager(long chatDelay) {
        this.chatDelay = chatDelay;
        Core.getInstance().getServer().getPluginManager().registerEvents(this, Core.getInstance().getPlugin());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        GeneralStats generalStats = new GeneralStats().getStatistics(event.getPlayer().getUniqueId(), true);
        Rank rank = RankManager.getInstance().getRank(generalStats.getRank());

        if(rank.getPriority() > 0) {
            if (floodMap.containsKey(event.getPlayer().getUniqueId())) {
                if (floodMap.get(event.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                    event.getPlayer().sendMessage(StringUtils.format(
                            ConfigLoader.getInstance().getConfig(generalStats.getChosenLanguage() + "-lang.yml").getString("chat-delay")
                    ));
                    return;
                }
            }
        }

        String prefix = generalStats.getPrefix().isEmpty() ? rank.getPrefix() : generalStats.getPrefix() + " ";

        String message = event.getMessage();
        if(event.getPlayer().isOp()) {
            message = StringUtils.format(message);
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(
                    StringUtils.format(prefix + generalStats.getDisplayedName() + "&f: ") + message
            );
        }

        floodMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis()+chatDelay);
    }
}
