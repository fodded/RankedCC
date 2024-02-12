package me.fodded.core.managers.stats;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;

public class Database {

    @Getter
    private MongoDatabase mongoDatabase;
    @Getter
    private static Database instance;

    public Database(String connectionString) {
        instance = this;

        MongoClient mongoClient = MongoClients.create(connectionString);
        mongoDatabase = mongoClient.getDatabase("statistics");
    }
}
