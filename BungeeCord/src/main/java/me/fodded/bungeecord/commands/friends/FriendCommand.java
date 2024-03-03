package me.fodded.bungeecord.commands.friends;

import me.fodded.bungeecord.managers.FriendManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend", "", "f");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            if(args.length == 1) {
                FriendManager.getInstance().proceedFriendRequest(player, args[0]);
                return;
            }

            if(args.length == 2) {
                switch(args[0]) {
                    case "remove":
                        FriendManager.getInstance().proceedFriendRemove(player, args[1]);
                        break;
                    case "add":
                        FriendManager.getInstance().proceedFriendRequest(player, args[1]);
                        break;
                    case "accept":
                        FriendManager.getInstance().acceptFriendRequest(player, args[1]);
                        break;
                    case "decline":
                        FriendManager.getInstance().declineFriendRequest(player, args[1]);
                        break;
                }
            }
        }
    }
}