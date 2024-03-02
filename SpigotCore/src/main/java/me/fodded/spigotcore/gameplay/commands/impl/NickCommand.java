package me.fodded.spigotcore.gameplay.commands.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import me.fodded.core.Core;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import me.fodded.core.model.Database;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import me.fodded.spigotcore.gameplay.player.AbstractServerPlayer;
import me.fodded.spigotcore.utils.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@CommandInfo(rank = Rank.YOUTUBE, name = "nick", usage = "&c/nick [player] [rank]", description = "Hides your original name")
public class NickCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reset")) {
                resetDisguisedNick(player);
                AbstractServerPlayer.sendLogToPlayers("&6[LOG] &fPlayer " + player.getName() + " &freset his disguise");
                return;
            }
        }

        if(args.length != 2) {
            sender.sendMessage(StringUtils.format(getUsage()));
            return;
        }

        setDisguisedNick(player, args);
    }

    private void setDisguisedNick(Player player, String[] args) {
        String disguisedNick = args[0];
        Rank disguisedRank = Rank.getRank(args[1]);

        if(disguisedNick.length() < 3 || disguisedNick.length() > 16 || doesContainIllegalCharacters(disguisedNick)) {
            StringUtils.sendMessage(player, "disguise.nick-not-allowed");
            return;
        }

        if(disguisedRank == null) {
            StringUtils.sendMessage(player, "no-rank-found", args[1]);
            return;
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());
        if(disguisedRank.getPriority() > generalStats.getRank().getPriority()) {
            StringUtils.sendMessage(player, "disguise.rank-higher");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(SpigotCore.getInstance().getPlugin(), () -> {
            if(DatabaseOperations.getInstance().doesCollectionHaveFieldValue("GeneralStats", "lastName", disguisedNick)) {
                StringUtils.sendMessage(player, "disguise.player-exists");
                return;
            }

            if(isDisguiseAlreadyUsed(disguisedNick)) {
                StringUtils.sendMessage(player, "disguise.player-exists");
                return;
            }

            Bukkit.getScheduler().runTask(SpigotCore.getInstance().getPlugin(), () -> {
                AbstractServerPlayer.sendLogToPlayers("&6[LOG] &fPlayer " + player.getName() + " &fchanged his nick to &6" + disguisedNick);
                DisguiseManager.getInstance().setDisguise(player, disguisedNick);
                addDisguiseToHistory(player, disguisedNick, disguisedRank);
                GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), updatedGeneralStats -> updatedGeneralStats.setDisguisedRank(disguisedRank));
            });
        });
    }

    private void addDisguiseToHistory(Player player, String disguisedNick, Rank disguisedRank) {
        CompletableFuture.runAsync(() -> {
            MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
            MongoCollection<Document> collection = mongoDatabase.getCollection("DisguiseHistory");

            Document document = new Document("uniqueId", player.getUniqueId().toString())
                    .append("disguisedNick", disguisedNick)
                    .append("disguisedRank", disguisedRank.name())
                    .append("time", System.currentTimeMillis());

            collection.insertOne(document);
        });
    }

    private boolean isDisguiseAlreadyUsed(String disguisedName) {
        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("DisguiseHistory");

        Pattern pattern = Pattern.compile(Pattern.quote(disguisedName), Pattern.CASE_INSENSITIVE);
        Document document = collection.find(
                new Document("disguisedNick", pattern)
        ).sort(Sorts.descending("time")).first();

        if(document == null) {
            return false;
        }

        UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRemoteValue(uniqueId);

        return generalStats.getDisguisedName().equalsIgnoreCase(disguisedName);
    }

    private void resetDisguisedNick(Player player) {
        DisguiseManager disguiseManager = DisguiseManager.getInstance();
        disguiseManager.setDisguise(player, disguiseManager.getNameFromUUID(player.getUniqueId()));

        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), updatedGeneralStats -> updatedGeneralStats.setDisguisedName(""));
    }

    public boolean doesContainIllegalCharacters(String input) {
        String legalCharactersRegex = "^[a-zA-Z0-9_]*$";
        return !input.matches(legalCharactersRegex);
    }
}

