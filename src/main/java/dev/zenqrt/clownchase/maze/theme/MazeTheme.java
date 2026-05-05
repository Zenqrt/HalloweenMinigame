package dev.zenqrt.clownchase.maze.theme;

import dev.zenqrt.clownchase.maze.theme.ground.MazeGroundDecoration;
import dev.zenqrt.clownchase.maze.theme.wall.MazeWallDecoration;
import org.bukkit.block.Biome;

public interface MazeTheme<G extends MazeGroundDecoration, W extends MazeWallDecoration> {

    G groundDecoration();
    W wallDecoration();
    Biome biome();
}
