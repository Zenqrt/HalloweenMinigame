package dev.zenqrt.clownchase.maze.theme.ground;

import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.world.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;

import java.util.Map;
import java.util.function.Function;

public interface MazeGroundDecoration {

    void createGround(BlockBatch batch, BlockPosition position, int length, int width, int depth);

    Map<String, Function<JsonObject, MazeGroundDecoration>> REGISTRY = Map.of(
            "solid", SolidGroundDecoration::fromJson,
            "mixture", MixtureGroundDecoration::fromJson
    );

}
