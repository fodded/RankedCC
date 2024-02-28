package me.fodded.spigotcore.gameplay.commands.impl;

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
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@CommandInfo(rank = Rank.YOUTUBE, name = "revealnick", usage = "&c/revealnick [disguised nick] [disguised rank]", description = "Shows player's original name")
public class RevealNickCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length != 2) {
            sender.sendMessage(StringUtils.format(getUsage()));
            return;
        }

        Rank disguisedRank = Rank.getRank(args[1]);
        if(disguisedRank == null) {
            sender.sendMessage(StringUtils.format("Couldn't find rank named " + args[1]));
            return;
        }

        CompletableFuture.runAsync(() -> {
            findDisguisedPlayer(sender, args[0], disguisedRank);
        });
    }

    private void findDisguisedPlayer(CommandSender sender, String disguisedName, Rank disguisedRank) {
        MongoDatabase mongoDatabase = Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB);
        MongoCollection<Document> collection = mongoDatabase.getCollection("DisguiseHistory");

        Pattern pattern = Pattern.compile(Pattern.quote(disguisedName), Pattern.CASE_INSENSITIVE);
        Document document = collection.find(
                new Document("disguisedNick", pattern).append("disguisedRank", disguisedRank.name())
        ).sort(Sorts.descending("time")).first();

        if(document == null) {
            sender.sendMessage(StringUtils.format("&cNo one has ever used disguise " + disguisedName + " with rank " + disguisedRank.name()));
            return;
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRemoteValue(UUID.fromString(document.getString("uniqueId")));
        sender.sendMessage(StringUtils.format(
                "&fThe last player who used disguise " + disguisedName + " was &6" + generalStats.getRank().getPrefix() + generalStats.getLastName()
        ));
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

