package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public interface MazeWallDecoration {

    void createVerticalWall(Instance instance, Pos pos);
    void createBottomHorizontalWall(Instance instance, Pos pos);
    void createTopHorizontalWall(Instance instance, Pos pos);

}
