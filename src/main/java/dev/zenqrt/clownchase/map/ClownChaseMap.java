package dev.zenqrt.clownchase.map;

import com.google.gson.annotations.SerializedName;
import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import org.bukkit.block.Biome;

public record ClownChaseMap(@SerializedName("display_name") String displayName,
                            @SerializedName("maze_theme") MazeTheme<?, ?> mazeTheme,
                            Biome biome) {
}
