package me.fodded.bungeecord.redislisteners;

import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SendLogsToPlayerListener implements IBungeeRedisListener {

    @Override
    public void onMessage(CharSequence channel, Object msg) {
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRedisValue(player.getUniqueId());

            if(generalStats == null) continue;
            if(!generalStats.isLogging()) continue;

            player.sendMessage(StringUtils.format(msg.toString()));
        }
    }
}
