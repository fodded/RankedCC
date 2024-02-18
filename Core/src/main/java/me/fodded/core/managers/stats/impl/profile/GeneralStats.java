package me.fodded.core.managers.stats.impl.profile;

import lombok.Data;
import me.fodded.core.managers.ranks.Rank;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public class GeneralStats {

    private final UUID uniqueId;
    private Rank rank;

    private String prefix, displayedName, lastName, chosenLanguage;
    private boolean vanished, logging, playersVisibility, chatEnabled;

    private long lastLogin, firstLogin;

    private List<UUID> friendList = new LinkedList<>();

    public GeneralStats(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.rank = Rank.DEFAULT;

        this.prefix = "";
        this.lastName = "";
        this.displayedName = "";
        this.chosenLanguage = "ru";

        this.chatEnabled = true;
        this.playersVisibility = true;

        this.firstLogin = System.currentTimeMillis();
    }
}