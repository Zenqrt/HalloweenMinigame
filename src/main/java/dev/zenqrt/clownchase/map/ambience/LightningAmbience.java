package dev.zenqrt.clownchase.map.ambience;

import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import io.papermc.paper.math.Position;

import java.util.concurrent.ThreadLocalRandom;

public final class LightningAmbience implements Ambience {

    public static LightningAmbience fromJson(JsonObject jsonObject) {
        float spawnChance = jsonObject.get("spawn_chance").getAsFloat();

        return new LightningAmbience(spawnChance);
    }

    private static final Position position = Position.block(0, 200, 0);
    private final float spawnChance;

    public LightningAmbience(float spawnChance) {
        this.spawnChance = spawnChance;
    }

    @Override
    public void tick(ClownChaseGame game) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float roll = random.nextFloat();

        if (roll > spawnChance)
            return;

        game.getGameWorld().strikeLightningEffect(position.offset(0, -100, 0).toLocation(game.getGameWorld()));
    }
}
