package dev.zenqrt.clownchase.maze.theme.ground;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.maze.theme.MixtureDecoration;
import dev.zenqrt.clownchase.world.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record MixtureGroundDecoration(
        List<DecorationEntry> decorationEntries) implements MazeGroundDecoration, MixtureDecoration {

    public static MixtureGroundDecoration fromJson(JsonObject jsonObject) {
        JsonArray jsonArray = jsonObject.getAsJsonArray("blocks");
        List<DecorationEntry> decorationEntries = new ArrayList<>();

        for (JsonElement element : jsonArray) {
            JsonObject elementObject = element.getAsJsonObject();

            decorationEntries.add(new DecorationEntry(
                    Bukkit.createBlockData(elementObject.get("block").getAsString()),
                    elementObject.get("weight").getAsInt()
            ));
        }

        return new MixtureGroundDecoration(decorationEntries);
    }

    @Override
    public void createGround(BlockBatch batch, BlockPosition position, int length, int width, int depth) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for(int y = 0; y < depth; y++) {
                    batch.setBlock(position.offset(x, -y, z), chooseBlockData(random));
                }
            }
        }
    }
}
