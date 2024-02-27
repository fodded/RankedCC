package me.fodded.core.managers.stats.operators;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import me.fodded.core.Core;
import me.fodded.core.model.Database;
import org.bson.Document;

import java.util.regex.Pattern;

public class DatabaseOperations {

    private static DatabaseOperations instance;
    public DatabaseOperations() {
        instance = this;
    }

    public boolean doesCollectionHaveFieldValue(String collectionName, String field, Object value) {
        MongoCollection<Document> collection = Core.getInstance().getDatabase()
                .getMongoDatabase(Database.STATISTICS_DB)
                .getCollection(collectionName);

        if(value instanceof String) {
            Pattern pattern = Pattern.compile(Pattern.quote((String) value), Pattern.CASE_INSENSITIVE);
            return collection.countDocuments(new Document(field, pattern)) > 0;
        }

        return collection.countDocuments(Filters.eq(field, value)) > 0;
    }

    /*
    public void updateStatistic(UUID uniqueId, String collectionName, String statistic, Object value) {
        MongoCollection<Document> collection = Core.getInstance().getDatabase().getMongoClient()
                .getDatabase("statistics")
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
        MongoCollection<Document> collection = Core.getInstance().getDatabase().getMongoClient()
                .getDatabase("statistics")
                .getCollection(collectionName);

        Document playerDocument = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if(playerDocument == null) {
            return false;
        }

        return playerDocument.containsKey(statistic);
    } */

    public static DatabaseOperations getInstance() {
        if(instance == null) {
            return new DatabaseOperations();
        }

        return instance;
    }
}
