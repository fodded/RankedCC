package me.fodded.core.managers.stats.impl.profile;

import lombok.Data;
import me.fodded.core.managers.ranks.Rank;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public class GeneralStats {

    private final UUID uniqueId;
    private Rank rank, disguisedRank;

    private String prefix, disguisedName, lastName, disguisedSkinTexture, disguisedSkinSignature, chosenLanguage, lastLobby, ipAddress;
    private boolean vanished, logging, playersVisibility, chatEnabled, friendRequestsEnabled, privateMessagesEnabled;

    private long lastLogin, firstLogin, timePlayed;

    private List<UUID> friendList = new LinkedList<>();
    private List<UUID> ignoreList = new LinkedList<>();

    public GeneralStats(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.rank = Rank.DEFAULT;
        this.disguisedRank = Rank.DEFAULT;

        this.prefix = "";
        this.lastName = "";
        this.disguisedName = "";
        this.chosenLanguage = "english";
        this.lastLobby = "Main";

        this.chatEnabled = true;
        this.playersVisibility = true;
        this.friendRequestsEnabled = true;
        this.privateMessagesEnabled = true;

        this.firstLogin = System.currentTimeMillis();
    }

    public void addFriendToFriendList(UUID uniqueId) {
        friendList.add(uniqueId);
    }

    public void removeFriendFromFriendList(UUID uniqueId) {
        friendList.remove(uniqueId);
    }

    public void addFriendToIgnoreList(UUID uniqueId) {
        friendList.add(uniqueId);
    }

    public void removeFriendFromIgnoreList(UUID uniqueId) {
        friendList.remove(uniqueId);
    }

    public void clearFriendList() {
        friendList.clear();
    }
}
