package dev.zenqrt.game.halloween.maze.themes.ground;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class SolidGroundDecoration implements MazeGroundDecoration {

    private final Block block;

    public SolidGroundDecoration(Block block) {
        this.block = block;
    }

    @Override
    public void createGround(Instance instance, Pos pos) {

    }
}
