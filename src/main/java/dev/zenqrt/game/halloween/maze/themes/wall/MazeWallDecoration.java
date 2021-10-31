package dev.zenqrt.game.halloween.maze.themes.wall;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;

public interface MazeWallDecoration {

    void createRightVerticalWall(AbsoluteBlockBatch batch, Point point);
    void createLeftVerticalWall(AbsoluteBlockBatch batch, Point point);
    void createBottomHorizontalWall(AbsoluteBlockBatch batch, Point point);
    void createTopHorizontalWall(AbsoluteBlockBatch batch, Point point);

    int getLength();
    int getWidth();
    int getHeight();

}
