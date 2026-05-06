package dev.zenqrt.clownchase.world.block;

import io.papermc.paper.math.BlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BlockBatch {

    private final Map<BlockData, Integer> paletteIndexMap = new HashMap<>();
    private final List<BlockData> palette = new ArrayList<>();
    private final List<StoredBlock> blocks = new ArrayList<>();

    public void setBlock(BlockPosition position, BlockData blockData) {
        Integer paletteIndex = paletteIndexMap.get(blockData);

        if (paletteIndex == null) {
            paletteIndex = palette.size();
            palette.add(blockData);
            paletteIndexMap.put(blockData, paletteIndex);
        }

        blocks.add(new StoredBlock(position.blockX(), position.blockY(), position.blockZ(), paletteIndex));
    }

    public void apply(World world, BlockPosition origin) {
        ServerLevel level = ((CraftWorld) world).getHandle();

        for (StoredBlock storedBlock : blocks) {
            BlockPos blockPos = new BlockPos(storedBlock.x, storedBlock.y, storedBlock.z)
                    .offset(origin.blockX(), origin.blockY(), origin.blockZ());
            LevelChunk chunk = level.getChunkAt(blockPos);

            BlockData blockData = palette.get(storedBlock.paletteIndex);

            chunk.setBlockState(blockPos, ((CraftBlockData)blockData).getState(), 0);
        }
    }

    record StoredBlock(int x, int y, int z, int paletteIndex) {}
}
