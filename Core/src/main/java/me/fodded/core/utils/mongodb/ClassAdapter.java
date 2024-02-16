package me.fodded.core.utils.mongodb;

import com.google.gson.*;

import java.lang.reflect.Type;

@SuppressWarnings("rawtypes")
public class ClassAdapter implements JsonSerializer<Class>, JsonDeserializer<Class> {
    @Override
    public Class deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return Class.forName(jsonElement.getAsJsonPrimitive().getAsString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonElement serialize(Class aClass, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(aClass.getName());
    }
}