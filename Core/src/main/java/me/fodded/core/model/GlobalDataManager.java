package me.fodded.core.model;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Preconditions;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.fodded.core.utils.mongodb.GsonSerializer;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.redisson.api.RMap;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class GlobalDataManager<K, V> {

    private final MongoCollection<V> mongoCollection;
    private final RMap<K, V> redissonMap;

    private final LoadingCache<K, V> loadingCache;
    private final Function<K, V> defaultProvider;

    public GlobalDataManager(MongoCollection<V> mongoCollection, RMap<K, V> redissonMap, Function<K, V> defaultProvider) {
        GsonSerializer gsonSerializer = GsonSerializer.getInstance();
        CodecRegistry codec = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), gsonSerializer.createCodecRegistry());

        this.redissonMap = redissonMap;
        this.mongoCollection = mongoCollection.withCodecRegistry(codec);

        loadingCache = Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build(this.redissonMap::get);

        this.defaultProvider = defaultProvider;
    }

    public void loadFromDatabaseToRedis(K key) {
        V value = getMongoValue(key);
        redissonMap.put(key, value);
    }

    private V getMongoValue(K key) {
        V value = mongoCollection.find(Filters.eq(key)).first();
        if(value == null) {
            value = defaultProvider.apply(key);
        }
        return value;
    }

    public void unloadFromRedisToDatabase(K key) {
        V value = redissonMap.remove(key);
        insertIntoDatabase(key, value);
    }

    private void insertIntoDatabase(K key, V value) {
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        this.mongoCollection.replaceOne(Filters.eq(key), value, options);
    }

    public void applyChangeToRedis(K key, Consumer<V> kConsumer) {
        V value = redissonMap.get(key);
        Preconditions.checkState(value != null, "data is not present in redis to be updated");
        kConsumer.accept(value);

        redissonMap.put(key, value);
    }

    public void applyChangeToDatabase(K key, Consumer<V> kConsumer) {
        V value = getMongoValue(key);
        kConsumer.accept(value);
        insertIntoDatabase(key, value);
    }

    public void applyChange(K key, Consumer<V> kConsumer) {
        if(isInRedis(key)) {
            applyChangeToRedis(key, kConsumer);
        } else {
            applyChangeToDatabase(key, kConsumer);
        }

        loadingCache.refresh(key);
    }

    public void removeFromCache(K key) {
        loadingCache.invalidate(key);
        loadingCache.asMap().remove(key);
    }

    public V getRemoteValue(K key) {
        if(isInRedis(key)) {
            return redissonMap.get(key);
        } else {
            return getMongoValue(key);
        }
    }

    public V getCachedValue(K key) {
        return loadingCache.get(key);
    }

    public boolean isInDatabase(K key) {
        return mongoCollection.countDocuments(Filters.eq(key)) == 0;
    }

    public boolean isInRedis(K key) {
        return redissonMap.containsKey(key);
    }

    public boolean isInCache(K key) {
        return loadingCache.getIfPresent(key) != null;
    }
}
