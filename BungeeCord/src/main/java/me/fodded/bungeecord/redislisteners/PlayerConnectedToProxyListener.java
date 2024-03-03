package me.fodded.bungeecord.redislisteners;

import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

// This particular redis listener is needed to make sure the data has been loaded to redis when we try to access it
public class PlayerConnectedToProxyListener implements IBungeeRedisListener {

    @Override
    public void onMessage(CharSequence channel, Object msg) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(msg.toString());
        if(player == null) return;

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRedisValue(player.getUniqueId());
        for(ProxiedPlayer eachPlayer : ProxyServer.getInstance().getPlayers()) {
            if(!generalStats.getFriendList().contains(eachPlayer.getUniqueId())) {
                continue;
            }

            String prefix = StringUtils.getPlayerPrefix(player);
            eachPlayer.sendMessage(StringUtils.format(
                    StringUtils.getMessage(eachPlayer, "friends.player-joined-server").replace("%player%", prefix))
            );
        }
    }
}
