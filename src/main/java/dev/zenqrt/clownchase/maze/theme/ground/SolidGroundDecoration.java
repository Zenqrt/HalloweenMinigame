package dev.zenqrt.clownchase.maze.theme.ground;

import dev.zenqrt.clownchase.world.generator.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.block.data.BlockData;

public final class SolidGroundDecoration implements MazeGroundDecoration {

    private final BlockData blockData;

    public SolidGroundDecoration(BlockData blockData) {
        this.blockData = blockData;
    }

    @Override
    public void createGround(BlockBatch batch, BlockPosition position, int length, int width, int depth) {
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < depth; y++) {
                    batch.setBlock(position.offset(x, -y, z), blockData);
                }
            }
        }
    }
}
