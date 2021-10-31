package dev.zenqrt.game.halloween.maze.themes.ground;

import dev.zenqrt.game.halloween.maze.themes.MixtureDecoration;
import kotlin.Pair;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MixtureGroundDecoration implements MazeGroundDecoration, MixtureDecoration {

    private final List<Pair<Block, Float>> blocks = new ArrayList<>();

    public MixtureGroundDecoration(List<Pair<Block, Float>> blocks) {
        this.blocks.addAll(blocks);
    }

    @SafeVarargs
    public MixtureGroundDecoration(Pair<Block, Float>... blocks) {
        this(List.of(blocks));
    }

    @Override
    public void createGround(AbsoluteBlockBatch batch, Point point, int length, int width, int depth) {
        var random = ThreadLocalRandom.current();
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for(int y = 0; y < depth; y++) {
                    batch.setBlock(point.add(x, -y, z), chooseBlock(random));
                }
            }
        }
    }

    @Override
    public List<Pair<Block, Float>> getBlocks() {
        return blocks;
    }
}
