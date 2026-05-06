package dev.zenqrt.clownchase.maze.theme.ground;

import dev.zenqrt.clownchase.world.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;

public interface MazeGroundDecoration {

    void createGround(BlockBatch batch, BlockPosition position, int length, int width, int depth);

}
