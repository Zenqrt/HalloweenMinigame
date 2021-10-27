package dev.zenqrt.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import net.minestom.server.world.DimensionType;

public interface MinecraftWorld {

    Tag<Double> SPAWN_POSITION_X_TAG = Tag.Double("SpawnPositionX");
    Tag<Double> SPAWN_POSITION_Y_TAG = Tag.Double("SpawnPositionY");
    Tag<Double> SPAWN_POSITION_Z_TAG = Tag.Double("SpawnPositionZ");
    Tag<Float> SPAWN_POSITION_YAW_TAG = Tag.Float("SpawnPositionYaw");
    Tag<Float> SPAWN_POSITION_PITCH_TAG = Tag.Float("SpawnPositionPitch");

    Pos getSpawnPosition();
    DimensionType getDimensionType();
    IChunkLoader getChunkLoader();

    default void setChunkLoader(InstanceContainer instance) {
        instance.setChunkLoader(getChunkLoader());
    }

    default InstanceContainer createInstanceContainer() {
        var instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(getDimensionType());
        instanceContainer.setChunkLoader(getChunkLoader());
        var spawnPosition = getSpawnPosition();
        instanceContainer.setTag(SPAWN_POSITION_X_TAG, spawnPosition.x());
        instanceContainer.setTag(SPAWN_POSITION_Y_TAG, spawnPosition.y());
        instanceContainer.setTag(SPAWN_POSITION_Z_TAG, spawnPosition.z());
        instanceContainer.setTag(SPAWN_POSITION_YAW_TAG, spawnPosition.yaw());
        instanceContainer.setTag(SPAWN_POSITION_PITCH_TAG, spawnPosition.pitch());
        return instanceContainer;
    }

}
