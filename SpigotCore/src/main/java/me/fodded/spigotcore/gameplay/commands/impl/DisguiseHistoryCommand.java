package me.fodded.spigotcore.gameplay.commands.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import me.fodded.core.Core;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.model.Database;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.utils.StringUtils;
import org.bson.Document;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@CommandInfo(rank = Rank.MODERATOR, name = "disguisehistory", usage = "&c/disguisehistory [disguised nick] [disguised rank]", description = "Shows players original names")
public class DisguiseHistoryCommand extends PluginCommand {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MMM-yy HH:mm:ssZ");

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        switch(args.length) {
            case 0:
                CompletableFuture.runAsync(() -> {
                    findDisguisedPlayers(sender);
                });
                break;
            case 1:
                CompletableFuture.runAsync(() -> {
                    findDisguisedPlayers(sender, args[0]);
                });
                break;
            case 2:
                Rank disguisedRank = Rank.getRank(args[1]);
                if (disguisedRank == null) {
                    sender.sendMessage(StringUtils.format("Couldn't find rank named " + args[1]));
                    return;
                }

                CompletableFuture.runAsync(() -> {
                    findDisguisedPlayers(sender, args[0], disguisedRank);
                });
                break;
            default:
                sender.sendMessage(StringUtils.format(getUsage()));
                break;
        }
    }

    private void findDisguisedPlayers(CommandSender sender, String disguisedName, Rank disguisedRank) {
        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("DisguiseHistory");

        Pattern pattern = Pattern.compile(Pattern.quote(disguisedName), Pattern.CASE_INSENSITIVE);
        FindIterable<Document> documents = collection.find(
                new Document("disguisedNick", pattern).append("disguisedRank", disguisedRank.name())
        ).sort(Sorts.descending("time")).limit(20);

        printDisguiseHistory(sender, documents);
    }

    private void findDisguisedPlayers(CommandSender sender, String disguisedName) {
        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("DisguiseHistory");

        Pattern pattern = Pattern.compile(Pattern.quote(disguisedName), Pattern.CASE_INSENSITIVE);
        FindIterable<Document> documents = collection.find(
                new Document("disguisedNick", pattern)
        ).sort(Sorts.descending("time")).limit(20);

        printDisguiseHistory(sender, documents);
    }

    private void findDisguisedPlayers(CommandSender sender) {
        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("DisguiseHistory");
        FindIterable<Document> documents = collection.find().sort(Sorts.descending("time")).limit(20);

        printDisguiseHistory(sender, documents);
    }

    private void printDisguiseHistory(CommandSender sender, FindIterable<Document> documents) {
        sender.sendMessage(StringUtils.format("&7"));
        int index = 1;
        for(Document document : documents) {
            UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
            String disguisedName = document.getString("disguisedNick");
            String disguisedRank = document.getString("disguisedRank");
            long time = document.getLong("time");

            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRemoteValue(uniqueId);

            String playerPrefix = generalStats.getRank().getPrefix() + generalStats.getLastName();
            String disguisePrefix = Objects.requireNonNull(Rank.getRank(disguisedRank)).getPrefix() + disguisedName;

            sender.sendMessage(StringUtils.format(
                    "&f#" + index++ + " " + playerPrefix + "&f disguised as " + disguisePrefix + " &fat " + simpleDateFormat.format(time)
            ));
        }
        sender.sendMessage(StringUtils.format("&7"));
    }
}
