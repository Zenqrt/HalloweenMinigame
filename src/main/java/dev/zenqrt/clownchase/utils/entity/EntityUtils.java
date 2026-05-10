package dev.zenqrt.clownchase.utils.entity;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
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

    public static void addSharedFlag(byte sharedFlagBit, List<SynchedEntityData.DataValue<?>> dataValues) {
        EntityDataSerializer<Byte> serializer = EntityAccessor.DATA_SHARED_FLAGS_ID.serializer();
        int dataSharedFlagsId = EntityAccessor.DATA_SHARED_FLAGS_ID.id();

        for (int i = 0; i < dataValues.size(); i++) {
            SynchedEntityData.DataValue<?> value = dataValues.get(i);

            if (value.id() != dataSharedFlagsId)
                continue;

            byte flags = (byte) value.value();

            dataValues.set(i, new SynchedEntityData.DataValue<>(dataSharedFlagsId, serializer, (byte) (flags | sharedFlagBit)));
            return;
        }

        dataValues.add(new SynchedEntityData.DataValue<>(dataSharedFlagsId, serializer, sharedFlagBit));
    }

}
