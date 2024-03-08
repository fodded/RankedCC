package me.fodded.bungeecord.managers.punishments.bans;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.Core;
import me.fodded.core.model.Database;
import org.bson.Document;

import java.util.UUID;

public class BanManager {

    private static BanManager instance;
    public BanManager() {
        instance = this;
    }

    public void addBanToDatabase(String staffExecutedBan, UUID bannedPlayerUniqueId, long banTimeStamp, long banDuration, String banUniqueId) {
        MongoCollection<Document> collection = Core.getInstance().getDatabase()
                .getMongoDatabase(Database.STATISTICS_DB)
                .getCollection("BansHistory");

        Document document = new Document("uniqueId", bannedPlayerUniqueId.toString())
                .append("staffId", staffExecutedBan)
                .append("banId", banUniqueId)
                .append("banTimeStamp", banTimeStamp)
                .append("banDuration", banDuration);

        collection.insertOne(document);
    }

    public void removeBanFromDatabase(UUID bannedPlayerUniqueId) {
        MongoCollection<Document> collection = Core.getInstance().getDatabase()
                .getMongoDatabase(Database.STATISTICS_DB)
                .getCollection("BansHistory");

        Document foundBan = collection.find(Filters.eq("uniqueId", bannedPlayerUniqueId.toString())).first();
        if(foundBan != null) {
            collection.deleteOne(foundBan);
        }
    }

    public static String getBanMessage(long banDuration, String banId) {
        String banDurationString = StringUtils.getFormattedTime(banDuration);

        String banMessage = "\n&f&lAccount temporarily banned";
        banMessage += "\n";
        banMessage += "\n&fWe recently received a report for bad behaviour by your account. Our";
        banMessage += "\n&fmoderators have now reviewed your case and identified that it goes";
        banMessage += "\n&fagainst our fair gameplay standards";
        banMessage += "\n";
        banMessage += "\n&f&lYour ban expires in " + banDurationString;
        banMessage += "\n&fYour ban ID: " + banId;
        banMessage += "\n&f";
        banMessage += "\n&fMake an appeal at our discord server &f&lhttps://discord.gg/vvSZtQjshf";

        return banMessage;
    }

    public static BanManager getInstance() {
        if(instance == null) {
            return new BanManager();
        }
        return instance;
    }
}
