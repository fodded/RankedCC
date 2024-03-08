package me.fodded.bungeecord.commands.bans;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.Core;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import me.fodded.core.model.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class BanHistoryCommand extends Command {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MMM-yy HH:mm:ssZ");

    public BanHistoryCommand() {
        super("banhistory");
    }

    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            if(!Rank.hasPermission(Rank.ADMIN, ((ProxiedPlayer) sender).getUniqueId())) {
                return;
            }
        }

        if(args.length == 1) {
            findBannedPlayers(sender, args[0]);
        } else {
            findBannedPlayers(sender);
        }
    }

    private void findBannedPlayers(CommandSender sender, String bannedPlayerName) {
        UUID bannedPlayerUUID = DatabaseOperations.getInstance().getUniqueIdFromName(bannedPlayerName);
        if(bannedPlayerUUID == null) {
            sender.sendMessage(StringUtils.format("&cCouldn't find player " + bannedPlayerName));
            return;
        }

        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("BansHistory");

        FindIterable<Document> documents = collection.find(
                new Document("uniqueId", bannedPlayerUUID.toString())
        ).sort(Sorts.descending("banTimeStamp")).limit(20);

        printBansHistory(sender, documents);
    }

    private void findBannedPlayers(CommandSender sender) {
        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("BansHistory");
        FindIterable<Document> documents = collection.find().sort(Sorts.descending("banTimeStamp")).limit(20);

        printBansHistory(sender, documents);
    }

    private void printBansHistory(CommandSender sender, FindIterable<Document> documents) {
        sender.sendMessage(StringUtils.format("&7"));
        for(Document document : documents) {
            UUID uniqueId = UUID.fromString(document.getString("uniqueId"));

            String timeBanned = simpleDateFormat.format(document.getLong("banTimeStamp"));
            String banDuration = simpleDateFormat.format(document.getLong("banDuration"));

            String staffExecutedBanDisplayName = getStaffDisplayName(document.getString("staffId"));
            String bannedPlayerDisplayName = getBannedPlayerDisplayName(uniqueId);
            int playerBansInTotal = (int) DatabaseOperations.getInstance().getPlayerBansAmount(uniqueId);

            sender.sendMessage(StringUtils.format(
                    "&f#" + playerBansInTotal + " bans " + bannedPlayerDisplayName + "&f got banned at " + timeBanned + " for " + banDuration + " by " + staffExecutedBanDisplayName
            ));
        }
        sender.sendMessage(StringUtils.format("&7"));
    }

    private String getBannedPlayerDisplayName(UUID bannedPlayerUniqueId) {
        GeneralStats bannedPlayerGeneralStats = GeneralStatsDataManager.getInstance().getRemoteValue(bannedPlayerUniqueId);
        return bannedPlayerGeneralStats.getRank().getPrefix() + bannedPlayerGeneralStats.getLastName();
    }
    
    private String getStaffDisplayName(String staffExecutedBan) {
        if(!staffExecutedBan.equalsIgnoreCase("CONSOLE")) {
            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRemoteValue(UUID.fromString(staffExecutedBan));
            return generalStats.getRank().getPrefix() + generalStats.getLastName();
        }
        return staffExecutedBan;
    }
}
