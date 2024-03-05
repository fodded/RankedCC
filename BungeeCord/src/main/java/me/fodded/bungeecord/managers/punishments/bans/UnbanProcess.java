package me.fodded.bungeecord.managers.punishments.bans;

import java.util.UUID;

public class UnbanProcess extends BanManager implements IBanProcess {

    private static UnbanProcess instance;
    public UnbanProcess() {
        instance = this;
    }

    @Override
    public void process(String staffExecutedCommand, UUID bannedPlayerUniqueId) {

    }

    public static UnbanProcess getInstance() {
        if(instance == null) {
            return new UnbanProcess();
        }
        return instance;
    }
}
