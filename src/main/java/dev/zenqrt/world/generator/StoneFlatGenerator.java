package dev.zenqrt.world.generator;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class StoneFlatGenerator implements ChunkGenerator {

    @Override
    public void generateChunkData(@NotNull ChunkBatch chunkBatch, int i, int i1) {
        for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
            for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                for (byte y = 0; y < 40; y++) {
                    chunkBatch.setBlock(x, y, z, Block.STONE);
                }
            }
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
