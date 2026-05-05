package dev.zenqrt.clownchase.maze.theme.wall;

import dev.zenqrt.clownchase.world.generator.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;

public interface MazeWallDecoration {

    void createRightVerticalWall(BlockBatch batch, BlockPosition position);
    void createLeftVerticalWall(BlockBatch batch, BlockPosition position);
    void createBottomHorizontalWall(BlockBatch batch, BlockPosition position);
    void createTopHorizontalWall(BlockBatch batch, BlockPosition position);

    int length();
    int width();
    int height();

}
