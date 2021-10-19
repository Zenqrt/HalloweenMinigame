package dev.zenqrt.game.halloween.maze.themes;

import dev.zenqrt.game.halloween.maze.themes.ground.MazeGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.MazeWallDecoration;

public interface MazeTheme<G extends MazeGroundDecoration, W extends MazeWallDecoration> {

    G getGroundDecoration();
    W getWallDecoration();

}
