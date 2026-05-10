package dev.zenqrt.clownchase.map;

import com.google.gson.annotations.SerializedName;
import dev.zenqrt.clownchase.map.ambience.Ambience;
import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import org.bukkit.block.Biome;
import org.intellij.lang.annotations.Subst;

public record ClownChaseMap(@SerializedName("display_name") String displayName,
                            @SerializedName("maze_theme") MazeTheme<?, ?> mazeTheme,
                            @Subst("empty") Ambience ambience,
                            Biome biome,
                            @Subst("0") int time) {
}
