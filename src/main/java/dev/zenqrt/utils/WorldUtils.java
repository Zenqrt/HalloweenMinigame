package dev.zenqrt.utils;

import dev.zenqrt.server.MinestomServer;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class WorldUtils {

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
