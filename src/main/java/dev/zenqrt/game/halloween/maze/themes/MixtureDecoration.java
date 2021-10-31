package dev.zenqrt.game.halloween.maze.themes;

import kotlin.Pair;
import net.minestom.server.instance.block.Block;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public interface MixtureDecoration {

    List<Pair<Block, Float>> getBlocks();

    default Block chooseBlock(ThreadLocalRandom random) {
        var block = getBlocks().get(random.nextInt(getBlocks().size()));
        while(random.nextFloat() > block.component2()) {
            block = getBlocks().get(random.nextInt(getBlocks().size()));
        }

        return block.component1();
    }

}
