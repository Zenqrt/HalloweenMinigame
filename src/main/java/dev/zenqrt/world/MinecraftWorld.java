package dev.zenqrt.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;

public interface MinecraftWorld {

    DimensionType getDimensionType();
    IChunkLoader getChunkLoader();

    default void setChunkLoader(InstanceContainer instance) {
        instance.setChunkLoader(getChunkLoader());
    }

    default InstanceContainer createInstanceContainer() {
        var instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(getDimensionType());
        instanceContainer.setChunkLoader(getChunkLoader());
        return instanceContainer;
    }

}
