package dev.zenqrt.world.generator;

import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EmptyGenerator implements ChunkGenerator {

    @Override
    public void generateChunkData(@NotNull ChunkBatch chunkBatch, int i, int i1) {

    }

    @Override
    public void fillBiomes(@NotNull Biome[] biomes, int i, int i1) {
        Arrays.fill(biomes, Biome.PLAINS);
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return null;
    }
}
