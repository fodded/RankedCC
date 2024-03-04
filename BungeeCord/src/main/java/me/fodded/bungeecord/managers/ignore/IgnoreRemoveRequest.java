package me.fodded.bungeecord.managers.ignore;

import me.fodded.bungeecord.managers.IgnoreManager;
import me.fodded.bungeecord.managers.friends.FriendDeclineRequest;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class IgnoreRemoveRequest extends IgnoreManager implements IgnoreRequest {

    private static IgnoreRemoveRequest instance;
    public IgnoreRemoveRequest() {
        instance = this;
    }

    @Override
    public void process(ProxiedPlayer playerSentRequest, String playerReceivedRequestName) {
        UUID playerReceivedRequestUUID = DatabaseOperations.getInstance().getUniqueIdFromName(playerReceivedRequestName);
        if(playerReceivedRequestUUID == null) {
            StringUtils.sendMessage(playerSentRequest, "friends.no-such-player");
            return;
        }

        GeneralStats playerSentRequestStats = GeneralStatsDataManager.getInstance().getCachedValue(playerSentRequest.getUniqueId());
        if(!playerSentRequestStats.getIgnoreList().contains(playerReceivedRequestUUID)) {
            StringUtils.sendMessage(playerSentRequest, "ignore.not-ignored");
            return;
        }

        IgnoreManager ignoreManager = IgnoreManager.getInstance();
        ignoreManager.removeFromIgnoreList(playerSentRequest, playerReceivedRequestUUID);

        StringUtils.sendMessage(playerSentRequest, "ignore.ignore-removed");
    }

    public static IgnoreRemoveRequest getInstance() {
        if(instance == null) {
            return new IgnoreRemoveRequest();
        }
        return instance;
    }
}
