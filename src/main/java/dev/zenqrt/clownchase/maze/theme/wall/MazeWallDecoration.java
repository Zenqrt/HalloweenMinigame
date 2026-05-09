package dev.zenqrt.clownchase.maze.theme.wall;

import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.world.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;

import java.util.Map;
import java.util.function.Function;

public interface MazeWallDecoration {

    void createRightVerticalWall(BlockBatch batch, BlockPosition position);
    void createLeftVerticalWall(BlockBatch batch, BlockPosition position);
    void createBottomHorizontalWall(BlockBatch batch, BlockPosition position);
    void createTopHorizontalWall(BlockBatch batch, BlockPosition position);

    Map<String, Function<JsonObject, MazeWallDecoration>> REGISTRY = Map.of(
            "solid", SolidWallDecoration::fromJson,
            "mixture", MixtureWallDecoration::fromJson
    );

}
