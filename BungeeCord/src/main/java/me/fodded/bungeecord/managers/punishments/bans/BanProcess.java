package me.fodded.bungeecord.managers.punishments.bans;

import me.fodded.bungeecord.Main;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BanProcess extends BanManager implements IBanProcess {

    private static BanProcess instance;
    public BanProcess() {
        instance = this;
    }

    @Override
    public void process(String staffExecutedCommand, UUID bannedPlayerUniqueId) {
        ProxiedPlayer targetPlayer = Main.getInstance().getProxy().getPlayer(bannedPlayerUniqueId);
        CompletableFuture.runAsync(() -> {
            String banUniqueId = UUID.randomUUID().toString().split("-")[4];
            int bansAmount = (int) DatabaseOperations.getInstance().getPlayerBansAmount(bannedPlayerUniqueId);
            long banDurationMS = getBanDurationMS(bansAmount);

            if (targetPlayer != null) {
                targetPlayer.disconnect(StringUtils.format(BanManager.getBanMessage(banDurationMS, banUniqueId)));
            }

            addBanToDatabase(staffExecutedCommand, bannedPlayerUniqueId, System.currentTimeMillis(), banDurationMS, banUniqueId);
        });
    }

    private long getBanDurationMS(int bansAmount) {
        int banDays = getBanDurationDays(bansAmount);
        return 1000L*60*60*24*banDays-1000; // subtract 1 second
    }

    private int getBanDurationDays(int bansAmount) {
        switch (bansAmount) {
            case 0:
                return 30;
            case 1:
                return 90;
            case 2:
                return 180;
            default:
                return 360;
        }
    }

    public static BanProcess getInstance() {
        if(instance == null) {
            return new BanProcess();
        }
        return instance;
    }
}
