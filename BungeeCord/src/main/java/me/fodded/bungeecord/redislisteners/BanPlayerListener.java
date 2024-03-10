package me.fodded.bungeecord.redislisteners;

import me.fodded.bungeecord.Main;
import me.fodded.bungeecord.managers.punishments.bans.BanProcess;
import me.fodded.bungeecord.utils.StringUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BanPlayerListener implements IBungeeRedisListener {

    @Override
    public void onMessage(CharSequence channel, Object msg) {
        CompletableFuture.runAsync(() -> {
            String[] messageArr = msg.toString().split(":");
            UUID playerUniqueId = UUID.fromString(messageArr[1]);

            ProxiedPlayer bannedPlayer = Main.getInstance().getProxy().getPlayer(playerUniqueId);
            if(bannedPlayer == null) {
                return;
            }

            String staffExecutedBanName = messageArr[0];
            BanProcess.getInstance().process(staffExecutedBanName, playerUniqueId);

            ProxiedPlayer staffPlayer = Main.getInstance().getProxy().getPlayer(staffExecutedBanName);
            if(staffPlayer != null && !staffExecutedBanName.equalsIgnoreCase("CONSOLE")) {
                staffPlayer.sendMessage(StringUtils.format("&aYou've successfully banned " + bannedPlayer.getName()));
            }
        });
    }
}