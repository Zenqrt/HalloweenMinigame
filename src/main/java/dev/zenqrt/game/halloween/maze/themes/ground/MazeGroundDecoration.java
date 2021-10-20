package dev.zenqrt.game.halloween.maze.themes.ground;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public interface MazeGroundDecoration {

    void createGround(Instance instance, Pos pos, int length, int width);

}
