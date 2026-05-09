package dev.zenqrt.clownchase.maze.theme.wall;

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

public record MixtureWallDecoration(List<DecorationEntry> decorationEntries, int length, int width, int height) implements MazeWallDecoration, MixtureDecoration {

    public static MixtureWallDecoration fromJson(JsonObject jsonObject) {
        JsonArray jsonArray = jsonObject.getAsJsonArray("blocks");
        List<DecorationEntry> decorationEntries = new ArrayList<>();

        for (JsonElement element : jsonArray) {
            JsonObject elementObject = element.getAsJsonObject();

            decorationEntries.add(new DecorationEntry(
                    Bukkit.createBlockData(elementObject.get("block").getAsString()),
                    elementObject.get("weight").getAsInt()
            ));
        }

        int length = jsonObject.get("length").getAsInt();
        int width = jsonObject.get("width").getAsInt();
        int height = jsonObject.get("height").getAsInt();

        return new MixtureWallDecoration(decorationEntries, length, width, height);
    }

    @Override
    public void createRightVerticalWall(BlockBatch batch, BlockPosition position) {
        createVerticalWall(batch, position);
    }

    @Override
    public void createLeftVerticalWall(BlockBatch batch, BlockPosition position) {
        createVerticalWall(batch, position.offset(length - width, 0, 0));
    }

    private void createVerticalWall(BlockBatch batch, BlockPosition position) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(position.offset(x, y, z), chooseBlockData(random));
                }
            }
        }
    }

    @Override
    public void createBottomHorizontalWall(BlockBatch batch, BlockPosition position) {
        createHorizontalWall(batch, position);
    }

    @Override
    public void createTopHorizontalWall(BlockBatch batch, BlockPosition position) {
        createHorizontalWall(batch, position.offset(0, 0, length - width));
    }

    private void createHorizontalWall(BlockBatch batch, BlockPosition position) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(position.offset(x, y, z), chooseBlockData(random));
                }
            }
        }
    }
}
