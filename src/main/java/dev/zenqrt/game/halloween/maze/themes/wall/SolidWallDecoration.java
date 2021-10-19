package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
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
    public void createVerticalWall(Instance instance, Pos pos) {
        for(int x = 0; x < width; x++) {
            for(int z = 0; z < length; z++) {
                for(int y = 0; y < height; y++) {
                    instance.setBlock(pos.add(x,y,z), block);
                }
            }
        }
    }

    @Override
    public void createBottomHorizontalWall(Instance instance, Pos pos) {
        createHorizontalWall(instance, pos);
    }

    @Override
    public void createTopHorizontalWall(Instance instance, Pos pos) {
        createHorizontalWall(instance, pos.add(0, 0, length - width));
    }

    private void createHorizontalWall(Instance instance, Pos pos) {
        for(int x = 0; x < length; x++) {
            for(int z = 0; z < width; z++) {
                for(int y = 0; y < height; y++) {
                    instance.setBlock(pos.add(x, y, z), block);
                }
            }
        }
    }
}
