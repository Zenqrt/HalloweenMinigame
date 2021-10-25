package dev.zenqrt.game.halloween.maze.themes.ground;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

public class SolidGroundDecoration implements MazeGroundDecoration {

    private final Block block;

    public SolidGroundDecoration(Block block) {
        this.block = block;
    }

    @Override
    public void createGround(AbsoluteBlockBatch batch, Point point,  int length, int width) {
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                batch.setBlock(point.add(x, 0, z), block);
            }
        }
    }
}
