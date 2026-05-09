package dev.zenqrt.clownchase.maze.theme;

import org.bukkit.block.data.BlockData;

import java.util.List;
import java.util.Random;

public interface MixtureDecoration {

    List<DecorationEntry> decorationEntries();

    default BlockData chooseBlockData(Random random) {
        int totalWeight = decorationEntries().stream()
                .mapToInt(DecorationEntry::weight)
                .sum();

        int roll = random.nextInt(totalWeight);

        for (DecorationEntry entry : decorationEntries()) {
            roll -= entry.weight;

            if (roll < 0)
                return entry.blockData;
        }

        throw new IllegalStateException("Could not pick BlockType");
    }

    record DecorationEntry(BlockData blockData, int weight) {}

}
