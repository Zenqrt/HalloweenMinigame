package dev.zenqrt.clownchase.data.serializers;

import com.google.gson.*;
import dev.zenqrt.clownchase.maze.theme.SimpleMazeTheme;
import dev.zenqrt.clownchase.maze.theme.ground.MazeGroundDecoration;
import dev.zenqrt.clownchase.maze.theme.wall.MazeWallDecoration;

import java.lang.reflect.Type;

public final class MazeThemeSerializer implements JsonDeserializer<SimpleMazeTheme> {

    @Override
    public SimpleMazeTheme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        MazeGroundDecoration groundDecoration = deserializeGround(jsonObject.getAsJsonObject("ground_decoration"));
        MazeWallDecoration wallDecoration = deserializeWall(jsonObject.getAsJsonObject("wall_decoration"));

        return new SimpleMazeTheme(groundDecoration, wallDecoration);
    }

    private static MazeGroundDecoration deserializeGround(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();

        return MazeGroundDecoration.REGISTRY.get(type).apply(jsonObject);
    }

    private static MazeWallDecoration deserializeWall(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();

        return MazeWallDecoration.REGISTRY.get(type).apply(jsonObject);
    }
}
