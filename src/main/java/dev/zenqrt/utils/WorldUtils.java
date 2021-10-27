package dev.zenqrt.utils;

import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.world.MinecraftWorld;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class WorldUtils {

    public static Pos getInstanceSpawnpoint(Instance instance) {
        var x = Utils.getOrDefault(instance.getTag(MinecraftWorld.SPAWN_POSITION_X_TAG), 0D);
        var y = Utils.getOrDefault(instance.getTag(MinecraftWorld.SPAWN_POSITION_Y_TAG), 0D);
        var z = Utils.getOrDefault(instance.getTag(MinecraftWorld.SPAWN_POSITION_Z_TAG), 0D);
        var yaw = Utils.getOrDefault(instance.getTag(MinecraftWorld.SPAWN_POSITION_YAW_TAG), 0F);
        var pitch = Utils.getOrDefault(instance.getTag(MinecraftWorld.SPAWN_POSITION_PITCH_TAG), 0F);

        return new Pos(
                x,
                y,
                z,
                yaw,
                pitch
        );
    }

    @Nullable
    public static Path getWorldPath(String worldName) {
        var worldFolderUrl = MinestomServer.class.getClassLoader().getResource("worlds/" + worldName);
        if(worldFolderUrl == null) throw new IllegalArgumentException("World folder not found!");

        try {
            return Path.of(worldFolderUrl.toURI());
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
