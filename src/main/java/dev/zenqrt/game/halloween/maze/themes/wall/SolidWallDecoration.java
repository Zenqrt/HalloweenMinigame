package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

public class SolidWallDecoration implements MazeWallDecoration {

    private final Block block;
    private final int length,width,height;

    public SolidWallDecoration(Block block, int length, int width, int height) {
        this.block = block;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    public void createRightVerticalWall(AbsoluteBlockBatch batch, Point pos) {
        createVerticalWall(batch, pos);
    }

    @Override
    public void createLeftVerticalWall(AbsoluteBlockBatch batch, Point pos) {
        createVerticalWall(batch, pos.add(length - width, 0, 0));
    }

    private void createVerticalWall(AbsoluteBlockBatch batch, Point pos) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(pos.add(x, y, z), block);
                }
            }
        }
    }

    @Override
    public void createBottomHorizontalWall(AbsoluteBlockBatch batch, Point pos) {
        createHorizontalWall(batch, pos);
    }

    @Override
    public void createTopHorizontalWall(AbsoluteBlockBatch batch, Point pos) {
        createHorizontalWall(batch, pos.add(0, 0, length - width));
    }

    private void createHorizontalWall(AbsoluteBlockBatch batch, Point pos) {
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(pos.add(x, y, z), block);
                }
            }
        }
    }
}
