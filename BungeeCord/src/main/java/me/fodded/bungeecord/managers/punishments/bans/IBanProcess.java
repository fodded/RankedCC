package me.fodded.bungeecord.managers.punishments.bans;

import java.util.UUID;

public interface IBanProcess {

    void process(String staffExecutedCommand, UUID bannedPlayerUniqueId);

}
