package dev.zenqrt.world.worlds;

import dev.zenqrt.utils.WorldUtils;
import dev.zenqrt.world.MinecraftWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

import java.util.Objects;

public class HalloweenLobbyWorld implements MinecraftWorld {

    private static final DimensionType DIMENSION_TYPE = DimensionType.builder(NamespaceID.from("halloween_lobby"))
            .fixedTime(18000L)
            .build();

    static {
        MinecraftServer.getDimensionTypeManager().addDimension(DIMENSION_TYPE);
    }

    @Override
    public DimensionType getDimensionType() {
        return DIMENSION_TYPE;
    }

    @Override
    public IChunkLoader getChunkLoader() {
        return new AnvilLoader(Objects.requireNonNull(WorldUtils.getWorldPath("halloween_lobby")));
    }
}
