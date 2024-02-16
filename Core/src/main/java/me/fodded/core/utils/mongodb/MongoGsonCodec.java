package me.fodded.core.utils.mongodb;

import com.google.gson.*;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MongoGsonCodec<V> implements Codec<V> {

    private static final Map<Class<? extends Number>, BiConsumer<Number, BsonWriter>> NUM_WRITERS = new HashMap<Class<? extends Number>, BiConsumer<Number, BsonWriter>>() {{
        this.put(Double.class, (num, writer) -> writer.writeDouble(num.doubleValue()));
        this.put(Integer.class, (num, writer) -> writer.writeInt32(num.intValue()));
        this.put(Long.class, (num, writer) -> writer.writeInt64(num.longValue()));
        this.put(Float.class, (num, writer) -> writer.writeDouble(num.floatValue()));
        this.put(Short.class, (num, writer) -> writer.writeDouble(num.shortValue()));
        this.put(Byte.class, (num, writer) -> writer.writeDouble(num.byteValue()));
        this.put(BigInteger.class, (num, writer) -> writer.writeString(num.toString()));
        this.put(BigDecimal.class, (num, writer) -> writer.writeDecimal128(new Decimal128((BigDecimal) num)));
    }};

    private final Class<V> typeClass;
    private final GsonSerializer gsonSerializer;

    public MongoGsonCodec(Class<V> typeClass, GsonSerializer gsonSerializer) {
        this.typeClass = typeClass;
        this.gsonSerializer = gsonSerializer;
    }

    @Override
    public Class<V> getEncoderClass() {
        return this.typeClass;
    }

    @Override
    public V decode(BsonReader reader, DecoderContext decoderContext) {
        JsonObject rootObject = this.readObject(reader);
        return this.gsonSerializer.fromJsonTree(rootObject, this.typeClass);
    }

    @Override
    public void encode(BsonWriter writer, V value, EncoderContext encoderContext) {
        JsonElement jsonElement = this.gsonSerializer.toJsonTree(value);
        this.writeJsonElement(writer, jsonElement);
    }

    private void writeJsonElement(BsonWriter writer, JsonElement element) {
        if (element.isJsonObject()) {
            writer.writeStartDocument();

            element.getAsJsonObject().asMap().forEach((key, value) -> {
                writer.writeName(key);
                this.writeJsonElement(writer, value);
            });

            writer.writeEndDocument();
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (jsonPrimitive.isString()) {
                writer.writeString(jsonPrimitive.getAsString());
            } else if (jsonPrimitive.isNumber()) {
                Number jsonNumber = jsonPrimitive.getAsNumber();
                NUM_WRITERS.get(jsonNumber.getClass()).accept(jsonNumber, writer);
            } else if (jsonPrimitive.isBoolean()) {
                boolean jsonBoolean = jsonPrimitive.getAsBoolean();
                writer.writeBoolean(jsonBoolean);
            } else {
                throw new IllegalStateException("Json primitive is of unknown type.");
            }
        } else if (element.isJsonNull()) {
            writer.writeNull();
        } else if (element.isJsonArray()) {
            writer.writeStartArray();
            element.getAsJsonArray().forEach(value -> this.writeJsonElement(writer, value));
            writer.writeEndArray();
        } else {
            throw new IllegalStateException("Unidentified json type");
        }
    }

    private JsonObject readObject(BsonReader reader) {
        JsonObject object = new JsonObject();
        reader.readStartDocument();

        BsonType type;
        while ((type = reader.readBsonType()) != BsonType.END_OF_DOCUMENT) {
            String key = reader.readName();
            object.add(key, this.readElement(reader, type));
        }

        reader.readEndDocument();
        return object;
    }

    private JsonElement readElement(BsonReader reader, BsonType type) {
        switch (type) {
            case DOUBLE:
                return new JsonPrimitive(reader.readDouble());
            case STRING:
                return new JsonPrimitive(reader.readString());
            case DOCUMENT:
                return this.readObject(reader);
            case ARRAY:
                return this.readArray(reader);
            case BOOLEAN:
                return new JsonPrimitive(reader.readBoolean());
            case INT32:
                return new JsonPrimitive(reader.readInt32());
            case INT64:
                return new JsonPrimitive(reader.readInt64());
            case DECIMAL128:
                new JsonPrimitive(reader.readDecimal128());
            case NULL:
                reader.readNull();
                return JsonNull.INSTANCE;
            default:
                reader.skipValue();
                return JsonNull.INSTANCE;
        }
    }

    private JsonArray readArray(BsonReader reader) {
        JsonArray jsonArray = new JsonArray();
        reader.readStartArray();

        BsonType type;
        while ((type = reader.readBsonType()) != BsonType.END_OF_DOCUMENT) {
            jsonArray.add(this.readElement(reader, type));
        }

        reader.readEndArray();
        return jsonArray;
    }

}