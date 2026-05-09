package dev.zenqrt.clownchase.utils;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public final class EntityUtils {

    private EntityUtils() {}

    public static void showEntity(Entity entity, double x, double y, double z, ServerPlayer player) {
        ClientboundAddEntityPacket addPacket = new ClientboundAddEntityPacket(
                entity.getId(),
                entity.getUUID(),
                x, y, z,
                entity.getXRot(),
                entity.getYRot(),
                entity.getType(),
                0,
                Vec3.ZERO,
                entity.getYHeadRot()
        );
        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(entity.getId(), Objects.requireNonNull(entity.getEntityData().getNonDefaultValues(), "entityData"));

        ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(List.of(addPacket, dataPacket));
        player.connection.send(bundlePacket);
    }

    public static void hideEntity(Entity entity, ServerPlayer player) {
        ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(entity.getId());
        player.connection.send(removePacket);
    }

}
