package dev.zenqrt.game.halloween.maze.themes.ground;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;

public interface MazeGroundDecoration {

    void createGround(AbsoluteBlockBatch batch, Point point, int length, int width);

}
