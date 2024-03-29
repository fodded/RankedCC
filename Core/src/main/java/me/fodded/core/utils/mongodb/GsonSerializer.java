package me.fodded.core.utils.mongodb;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import me.fodded.core.utils.redis.RedisGsonCodec;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class GsonSerializer {

    private final Gson prettyProxy = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private final Set<Type> typeSet = new HashSet<>();
    private final Set<Class<?>> abstractRegistrations = new HashSet<>();
    private final Map<Object, Consumer<GsonBuilder>> builderConsumer = new HashMap<>();
    private final Map<Object, Gson> adapterSkipMap = new HashMap<>();
    private boolean changed = true;
    private Gson currentGson;

    @Getter
    private static final GsonSerializer instance = new GsonSerializer();

    private GsonSerializer() {
        this.registerTypeAdapter(UUID.class, new UUIDAdapter());
        this.registerTypeAdapter(Class.class, new ClassAdapter());
    }

    public <T> boolean hasAbstractRegistration(Class<T> type) {
        return this.abstractRegistrations.contains(type);
    }

    public Gson getGson() {
        this.checkForChanges();
        return this.currentGson;
    }

    public Gson getGsonWithout(Object adapter) {
        this.checkForChanges();
        return this.adapterSkipMap.get(adapter);
    }

    private void checkForChanges() {
        if (!this.changed) {
            return;
        }
        this.changed = false;
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization();
        this.builderConsumer.values().forEach(consumer -> consumer.accept(builder));
        this.currentGson = builder.create();

        for (Object adapter : new ArrayList<>(this.builderConsumer.keySet())) {
            GsonBuilder skipBuilder = new GsonBuilder();
            this.builderConsumer.forEach((cAdapter, consumer) -> {
                if (cAdapter != adapter) {
                    consumer.accept(skipBuilder);
                }
            });
            this.adapterSkipMap.put(adapter, skipBuilder.create());
        }
    }

    public void register(Object adapter, Consumer<GsonBuilder> consumer) {
        this.builderConsumer.put(adapter, consumer);
        this.changed = true;
    }

    public <T> String toJson(T object) {
        this.checkForChanges();
        return this.currentGson.toJson(object);
    }

    public <T> String pretty(T object) {
        return this.prettyProxy.toJson(this.toJsonTree(object));
    }

    public <T> JsonElement toJsonTree(T object) {
        this.checkForChanges();
        return this.currentGson.toJsonTree(object);
    }

    public <T> T fromJson(String json, Type type) {
        this.checkForChanges();
        return this.currentGson.fromJson(json, type);
    }

    public <T> T fromJsonTree(JsonElement json, Type type) {
        this.checkForChanges();
        return this.currentGson.fromJson(json, type);
    }

    public <T> void registerAbstractTypeHierarchyAdapter(Class<T> tClass) {
        this.abstractRegistrations.add(tClass);
        this.registerTypeHierarchyAdapter(tClass, new AbstractClassAdapter(this));
    }

    public <T> void registerAbstractTypeAdapter(Class<T> type) {
        this.abstractRegistrations.add(type);
        this.registerTypeAdapter(type, new AbstractClassAdapter(this));
    }

    public void registerTypeAdapterFactory(TypeAdapterFactory typeAdapterFactory) {
        this.register(typeAdapterFactory, builder -> builder.registerTypeAdapterFactory(typeAdapterFactory));
    }

    public void registerTypeAdapter(Type type, Object adapter) {
        this.typeSet.add(type);
        this.register(adapter, builder -> builder.registerTypeAdapter(type, adapter));
    }

    public <T> void registerTypeHierarchyAdapter(Class<T> tClass, Object adapter) {
        this.typeSet.add(tClass);
        this.register(adapter, builder -> builder.registerTypeHierarchyAdapter(tClass, adapter));
    }

    public <T> TypeAdapter<T> getAdapter(TypeToken<T> tTypeToken) {
        return this.getGson().getAdapter(tTypeToken);
    }

    public boolean hasTypeAdapter(Type type) {
        return this.typeSet.contains(type);
    }

    public List<Type> getRegisteredTypes() {
        return new ArrayList<>(this.typeSet);
    }

    public CodecRegistry createCodecRegistry() {
        return new GsonCodecRegistry(this);
    }

    public <T> void registerInstanceCreator(Class<T> type, InstanceCreator<T> multiMapInstanceCreator) {
        this.registerTypeAdapter(type, multiMapInstanceCreator);
    }

    public RedisGsonCodec createRedisCodec() {
        return new RedisGsonCodec(this);
    }
}