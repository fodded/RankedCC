package me.fodded.core.model;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;

public class Database {

    private MongoClient mongoClient;
    public static final String STATISTICS_DB = "statistics";

    public Database(String connectionString) {
        MongoClientSettings mongoSettings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        mongoClient = MongoClients.create(mongoSettings);
    }

    public MongoDatabase getMongoDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }
}
