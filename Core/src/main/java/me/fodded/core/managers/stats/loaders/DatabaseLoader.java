package me.fodded.core.managers.stats.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import me.fodded.core.managers.stats.Database;
import me.fodded.core.managers.stats.Statistics;
import org.bson.Document;

import java.util.UUID;

public class DatabaseLoader implements IStatisticsLoader {

    private static DatabaseLoader instance;
    public DatabaseLoader() {
        instance = this;
    }

    @Override
    public void uploadStatistics(UUID uniqueId, Statistics statistics) {
        MongoCollection<Document> collection = Database.getInstance().getMongoDatabase().getCollection(statistics.getClass().getSimpleName());

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(statistics);

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            collection.insertOne(Document.parse(json));
            return;
        }

        collection.replaceOne(playerDocument, Document.parse(json));
    }

    @Override
    public Statistics loadStatistics(UUID uniqueId, Class statisticsClass) {
        MongoCollection<Document> collection = Database.getInstance().getMongoDatabase().getCollection(statisticsClass.getSimpleName());
        Gson gson = new GsonBuilder().create();

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            return null;
        }

        return (Statistics) gson.fromJson(playerDocument.toJson(), statisticsClass);
    }

    public static DatabaseLoader getInstance() {
        if(instance == null) {
            return new DatabaseLoader();
        }

        return instance;
    }
}
