package dev.zenqrt.clownchase.map.ambience;

import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public final class LightningAmbience implements Ambience {

    public static LightningAmbience fromJson(JsonObject jsonObject) {
        float spawnChance = jsonObject.get("spawn_chance").getAsFloat();

        return new LightningAmbience(spawnChance);
    }

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

        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, ((CraftWorld) game.getGameWorld()).getHandle());
        lightningBolt.isEffect = true;

        for (ClownChasePlayer gamePlayer : game.getPlayers().values()) {
            Player player = gamePlayer.validatePlayer();
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

            ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                    lightningBolt.getId(),
                    lightningBolt.getUUID(),
                    player.getX(),
                    player.getY() + 150,
                    player.getZ(),
                    lightningBolt.getXRot(),
                    lightningBolt.getYRot(),
                    EntityType.LIGHTNING_BOLT,
                    0,
                    Vec3.ZERO,
                    lightningBolt.getYHeadRot()
            );
            nmsPlayer.connection.send(addEntityPacket);
        }

        lightningBolt.remove(Entity.RemovalReason.DISCARDED);
    }
}
