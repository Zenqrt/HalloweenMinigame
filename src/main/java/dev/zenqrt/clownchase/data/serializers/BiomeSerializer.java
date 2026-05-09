package dev.zenqrt.clownchase.data.serializers;

import com.google.gson.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Biome;
import org.intellij.lang.annotations.Subst;

import java.lang.reflect.Type;

public final class BiomeSerializer implements JsonDeserializer<Biome>, JsonSerializer<Biome> {

    @Override
    public Biome deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        @Subst("minecraft:plains") String keyString = jsonElement.getAsString();

        return RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).getOrThrow(Key.key(keyString));
    }

    @Override
    public JsonElement serialize(Biome biome, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(biome.getKey().asString());
    }
}
