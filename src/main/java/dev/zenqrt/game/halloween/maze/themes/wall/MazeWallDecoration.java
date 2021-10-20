package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public interface MazeWallDecoration {

    void createRightVerticalWall(Instance instance, Pos pos);
    void createLeftVerticalWall(Instance instance, Pos pos);
    void createBottomHorizontalWall(Instance instance, Pos pos);
    void createTopHorizontalWall(Instance instance, Pos pos);

}
