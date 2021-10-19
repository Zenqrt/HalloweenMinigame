package dev.zenqrt.game.halloween.maze.themes;

import dev.zenqrt.game.halloween.maze.themes.ground.SolidGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.SolidWallDecoration;
import net.minestom.server.instance.block.Block;

public class HedgeMazeTheme implements MazeTheme<SolidGroundDecoration, SolidWallDecoration> {

    private final SolidGroundDecoration groundDecoration;
    private final SolidWallDecoration wallDecoration;

    public HedgeMazeTheme(int length, int width, int height) {
        this.groundDecoration = new SolidGroundDecoration(Block.DIRT);
        this.wallDecoration = new SolidWallDecoration(Block.OAK_LEAVES, length, width, height);
    }

    @Override
    public SolidGroundDecoration getGroundDecoration() {
        return groundDecoration;
    }

    @Override
    public SolidWallDecoration getWallDecoration() {
        return wallDecoration;
    }
}
