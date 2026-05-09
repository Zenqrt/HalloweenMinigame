package dev.zenqrt.clownchase.maze.theme;

import dev.zenqrt.clownchase.maze.theme.ground.SolidGroundDecoration;
import dev.zenqrt.clownchase.maze.theme.wall.SolidWallDecoration;
import org.bukkit.block.data.BlockData;

public final class SolidMazeTheme implements MazeTheme<SolidGroundDecoration, SolidWallDecoration> {

    private final SolidGroundDecoration groundDecoration;
    private final SolidWallDecoration wallDecoration;

    public SolidMazeTheme(int length, int width, int height, BlockData groundBlock, BlockData wallBlock) {
        this.groundDecoration = new SolidGroundDecoration(groundBlock);
        this.wallDecoration = new SolidWallDecoration(wallBlock, length, width, height);
    }

    @Override
    public SolidGroundDecoration groundDecoration() {
        return groundDecoration;
    }

    @Override
    public SolidWallDecoration wallDecoration() {
        return wallDecoration;
    }

}
