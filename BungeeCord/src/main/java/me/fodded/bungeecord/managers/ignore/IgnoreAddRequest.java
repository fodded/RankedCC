package me.fodded.bungeecord.managers.ignore;

import me.fodded.bungeecord.managers.FriendManager;
import me.fodded.bungeecord.managers.IgnoreManager;
import me.fodded.bungeecord.managers.friends.FriendAcceptRequest;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class IgnoreAddRequest extends IgnoreManager implements IgnoreRequest {

    private static IgnoreAddRequest instance;
    public IgnoreAddRequest() {
        instance = this;
    }

    @Override
    public void process(ProxiedPlayer playerSentRequest, String playerReceivedRequestName) {
        ProxiedPlayer playerReceivedRequest = ProxyServer.getInstance().getPlayer(playerReceivedRequestName);
        if(playerReceivedRequest == null) {
            StringUtils.sendMessage(playerSentRequest, "friends.no-player-present");
            return;
        }

        if(isPlayerFlooding(playerSentRequest)) {
            StringUtils.sendMessage(playerSentRequest, "ignore.ignore-adding-too-often");
            return;
        }

        GeneralStats playerSentRequestGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(playerSentRequest.getUniqueId());
        if(playerSentRequestGeneralStats.getIgnoreList().size() >= 1000) {
            StringUtils.sendMessage(playerSentRequest, "ignore.ignore-limit");
            return;
        }

        IgnoreManager.getInstance().addToIgnoreList(playerSentRequest, playerReceivedRequest);
    }

    public static IgnoreAddRequest getInstance() {
        if(instance == null) {
            return new IgnoreAddRequest();
        }
        return instance;
    }
}
