package dev.zenqrt.clownchase.map.ambience;

import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.game.ClownChaseGame;

import java.util.Map;
import java.util.function.Function;

public interface Ambience {

    void tick(ClownChaseGame game);

    Map<String, Function<JsonObject, Ambience>> REGISTRY = Map.of(
            "empty", _ -> new EmptyAmbience(),
            "lightning", LightningAmbience::fromJson
    );

}
