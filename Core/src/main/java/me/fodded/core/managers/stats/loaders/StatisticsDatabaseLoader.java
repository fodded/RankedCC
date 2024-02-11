package me.fodded.core.managers.stats.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.managers.stats.Statistics;
import org.bson.Document;

import java.util.UUID;

public class StatisticsDatabaseLoader implements IStatisticsLoader {

    private MongoDatabase mongoDatabase;
    private static StatisticsDatabaseLoader instance;
    public StatisticsDatabaseLoader() {
        instance = this;
    }

    public void connectMongoDatabase() {
        String connectionString = ConfigLoader.getInstance().getConfig("core-config.yml").getString("mongodb-connection");

        MongoClient mongoClient = MongoClients.create(connectionString);
        mongoDatabase = mongoClient.getDatabase("statistics");
    }

    @Override
    public void uploadStatistics(UUID uniqueId, Statistics statistics) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(statistics.getClass().getSimpleName());

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
        MongoCollection<Document> collection = mongoDatabase.getCollection(statisticsClass.getSimpleName());
        Gson gson = new GsonBuilder().create();

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            return null;
        }

        return (Statistics) gson.fromJson(playerDocument.toJson(), statisticsClass);
    }

    public static StatisticsDatabaseLoader getInstance() {
        if(instance == null) {
            return new StatisticsDatabaseLoader();
        }

        return instance;
    }
}
