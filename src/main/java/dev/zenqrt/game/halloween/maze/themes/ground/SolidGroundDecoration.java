package dev.zenqrt.game.halloween.maze.themes.ground;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

public record SolidGroundDecoration(Block block) implements MazeGroundDecoration {

    @Override
    public void createGround(AbsoluteBlockBatch batch, Pos pos, int length, int width) {
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                batch.setBlock(pos.add(x, 0, z), block);
            }
        }
    }
}
