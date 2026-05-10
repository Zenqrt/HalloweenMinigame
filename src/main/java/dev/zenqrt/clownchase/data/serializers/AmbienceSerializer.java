package dev.zenqrt.clownchase.data.serializers;

import com.google.gson.*;
import dev.zenqrt.clownchase.map.ambience.Ambience;

import java.lang.reflect.Type;

public final class AmbienceSerializer implements JsonDeserializer<Ambience> {

    @Override
    public Ambience deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String key = jsonObject.get("type").getAsString();

        return Ambience.REGISTRY.get(key).apply(jsonObject);
    }
}
