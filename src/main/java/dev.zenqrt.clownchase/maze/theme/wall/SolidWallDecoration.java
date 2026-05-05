package dev.zenqrt.clownchase.maze.theme.wall;

import dev.zenqrt.clownchase.world.generator.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.block.data.BlockData;

public final class SolidWallDecoration implements MazeWallDecoration {

    private final BlockData blockData;
    private final int length, width, height;

    public SolidWallDecoration(BlockData blockData, int length, int width, int height) {
        this.blockData = blockData;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    public void createRightVerticalWall(BlockBatch batch, BlockPosition position) {
        createVerticalWall(batch, position);
    }

    @Override
    public void createLeftVerticalWall(BlockBatch batch, BlockPosition position) {
        createVerticalWall(batch, position.offset(length - width, 0, 0));
    }

    private void createVerticalWall(BlockBatch batch, BlockPosition position) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(position.offset(x, y, z), blockData);
                }
            }
        }
    }

    @Override
    public void createBottomHorizontalWall(BlockBatch batch, BlockPosition position) {
        createHorizontalWall(batch, position);
    }

    @Override
    public void createTopHorizontalWall(BlockBatch batch, BlockPosition position) {
        createHorizontalWall(batch, position.offset(0, 0, length - width));
    }

    private void createHorizontalWall(BlockBatch batch, BlockPosition position) {
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(position.offset(x, y, z), blockData);
                }
            }
        }
    }


    @Override
    public int length() {
        return 0;
    }

    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }
}
