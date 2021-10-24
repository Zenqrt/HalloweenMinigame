package dev.zenqrt.game.halloween.maze.themes.ground;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;

public interface MazeGroundDecoration {

    void createGround(AbsoluteBlockBatch batch, Pos pos, int length, int width);

}
