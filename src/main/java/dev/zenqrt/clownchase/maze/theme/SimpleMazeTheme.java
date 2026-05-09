package dev.zenqrt.clownchase.maze.theme;

import dev.zenqrt.clownchase.maze.theme.ground.MazeGroundDecoration;
import dev.zenqrt.clownchase.maze.theme.wall.MazeWallDecoration;

public record SimpleMazeTheme(MazeGroundDecoration groundDecoration, MazeWallDecoration wallDecoration) implements MazeTheme<MazeGroundDecoration, MazeWallDecoration> {
}
