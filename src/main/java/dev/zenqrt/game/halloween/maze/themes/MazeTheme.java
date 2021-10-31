package dev.zenqrt.game.halloween.maze.themes;

import dev.zenqrt.game.halloween.maze.themes.ground.MazeGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.MazeWallDecoration;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biomes.BiomeEffects;

public interface MazeTheme<G extends MazeGroundDecoration, W extends MazeWallDecoration> {

    G getGroundDecoration();
    W getWallDecoration();
    BiomeEffects getBiomeEffects();

}
