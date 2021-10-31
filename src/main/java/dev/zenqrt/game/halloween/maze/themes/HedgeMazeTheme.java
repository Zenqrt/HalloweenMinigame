package dev.zenqrt.game.halloween.maze.themes;

import dev.zenqrt.game.halloween.maze.themes.ground.MixtureGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.ground.SolidGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.MixtureWallDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.SolidWallDecoration;
import kotlin.Pair;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;

import java.awt.*;
import java.util.List;

public class HedgeMazeTheme implements MazeTheme<MixtureGroundDecoration, MixtureWallDecoration> {

    private final MixtureGroundDecoration groundDecoration;
    private final MixtureWallDecoration wallDecoration;

    public HedgeMazeTheme(int length, int width, int height) {
        this.groundDecoration = new MixtureGroundDecoration(new Pair<>(Block.GRASS_BLOCK, 0.65f), new Pair<>(Block.DIRT, 0.35f), new Pair<>(Block.GRAVEL, 0.35f), new Pair<>(Block.COARSE_DIRT, 0.35f));
        this.wallDecoration = new MixtureWallDecoration(List.of(new Pair<>(Block.OAK_LEAVES, 0.99f), new Pair<>(Block.PUMPKIN, 0.01f)), length, width, height);
    }

    @Override
    public MixtureGroundDecoration getGroundDecoration() {
        return groundDecoration;
    }

    @Override
    public MixtureWallDecoration getWallDecoration() {
        return wallDecoration;
    }

    @Override
    public BiomeEffects getBiomeEffects() {
        return BiomeEffects.builder()
                .grassColor(new Color(81, 110, 81).getRGB())
                .foliageColor(new Color(76, 92, 72).getRGB())
                .build();
    }
}
