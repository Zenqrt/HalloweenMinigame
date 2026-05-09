package dev.zenqrt.clownchase.utils.file;

import org.bukkit.Bukkit;

import java.nio.file.Path;

public final class Directories {

    public static final Path DIMENSIONS = Bukkit.getWorldContainer().toPath().resolve("world/dimensions/minecraft");

    private Directories() {
    }

}
