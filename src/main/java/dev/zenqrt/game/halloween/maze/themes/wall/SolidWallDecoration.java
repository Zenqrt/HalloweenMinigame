package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

public record SolidWallDecoration(Block block, int length, int width,
                                  int height) implements MazeWallDecoration {

    @Override
    public void createRightVerticalWall(AbsoluteBlockBatch batch, Pos pos) {
        createVerticalWall(batch, pos);
    }

    @Override
    public void createLeftVerticalWall(AbsoluteBlockBatch batch, Pos pos) {
        createVerticalWall(batch, pos.add(length - width, 0, 0));
    }

    private void createVerticalWall(AbsoluteBlockBatch batch, Pos pos) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(pos.add(x, y, z), block);
                }
            }
        }
    }

    @Override
    public void createBottomHorizontalWall(AbsoluteBlockBatch batch, Pos pos) {
        createHorizontalWall(batch, pos);
    }

    @Override
    public void createTopHorizontalWall(AbsoluteBlockBatch batch, Pos pos) {
        createHorizontalWall(batch, pos.add(0, 0, length - width));
    }

    private void createHorizontalWall(AbsoluteBlockBatch batch, Pos pos) {
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(pos.add(x, y, z), block);
                }
            }
        }
    }
}
