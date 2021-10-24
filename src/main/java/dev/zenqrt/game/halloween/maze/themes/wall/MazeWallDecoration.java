package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;

public interface MazeWallDecoration {

    void createRightVerticalWall(AbsoluteBlockBatch batch, Pos pos);
    void createLeftVerticalWall(AbsoluteBlockBatch batch, Pos pos);
    void createBottomHorizontalWall(AbsoluteBlockBatch batch, Pos pos);
    void createTopHorizontalWall(AbsoluteBlockBatch batch, Pos pos);

}
