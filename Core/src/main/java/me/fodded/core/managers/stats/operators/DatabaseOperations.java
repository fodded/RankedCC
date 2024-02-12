package me.fodded.core.managers.stats.operators;

import com.mongodb.client.MongoCollection;
import me.fodded.core.managers.stats.Database;
import org.bson.Document;

import java.util.UUID;

public class DatabaseOperations {

    private static DatabaseOperations instance;
    public DatabaseOperations() {
        instance = this;
    }

    public Object getStatistic(UUID uniqueId, Class statisticClass, String statistic) {
        MongoCollection<Document> collection = Database.getInstance()
                .getMongoDatabase()
                .getCollection(statisticClass.getClass().getSimpleName());

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            return null;
        }

        return playerDocument.get(statistic);
    }

    public void updateStatistic(UUID uniqueId, String collectionName, String statistic, Object value) {
        MongoCollection<Document> collection = Database.getInstance()
                .getMongoDatabase()
                .getCollection(collectionName);

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            return;
        }

        Document newStatistic = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        newStatistic.append(statistic, value);

        collection.replaceOne(playerDocument, newStatistic);
    }

    public boolean collectionExists(String collectionName) {
        for (String name : Database.getInstance().getMongoDatabase().listCollectionNames()) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean statisticExists(UUID uniqueId, String collectionName, String statistic) {
        MongoCollection<Document> collection = Database.getInstance()
                .getMongoDatabase()
                .getCollection(collectionName);

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            return false;
        }

        return playerDocument.containsKey(statistic);
    }

    public static DatabaseOperations getInstance() {
        if(instance == null) {
            return new DatabaseOperations();
        }

        return instance;
    }
}
