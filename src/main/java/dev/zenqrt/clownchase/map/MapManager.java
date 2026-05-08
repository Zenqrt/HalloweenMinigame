package dev.zenqrt.clownchase.map;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.utils.file.MyFileUtils;
import io.papermc.paper.math.Position;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class MapManager {

    private static final Path WORLDS_DIR = Bukkit.getWorldContainer().toPath().resolve("world/dimensions/minecraft");

    private final Map<Integer, World> gameWorlds = new HashMap<>();
    private final ClownChasePlugin plugin;

    public MapManager(ClownChasePlugin plugin) {
        this.plugin = plugin;
    }

    // This is not fully async. Everything but world creation is async
    public void createGameWorldAsync(int gameId, Consumer<World> onDone) {
        String worldName = "clown-chase_" + gameId;
        AtomicBoolean doneCopying = new AtomicBoolean(false);

        Bukkit.getAsyncScheduler().runNow(this.plugin, _ -> {
            try {
                MyFileUtils.copyResourceFolder("worlds/empty", WORLDS_DIR.resolve(worldName));
                doneCopying.set(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Bukkit.getScheduler().runTaskTimer(this.plugin, task -> {
            if (!doneCopying.get())
                return;

            World world = createWorldFast(worldName);

            if (world == null)
                throw new NullPointerException("world");

            world.setAutoSave(false);
            world.setGameRule(GameRules.SPAWN_MOBS, false);
            world.setGameRule(GameRules.ADVANCE_TIME, false);
            world.setGameRule(GameRules.ADVANCE_WEATHER, false);
            world.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRules.FALL_DAMAGE, false);
            world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);

            this.gameWorlds.put(gameId, world);

            onDone.accept(world);

            task.cancel();
        }, 0, 10);
    }

    private static World createWorldFast(String worldName) {
        return WorldCreator
                .name(worldName)
                .forcedSpawnPosition(Position.BLOCK_ZERO, 0, 0) // <--- This is what makes it go fast
                .createWorld();
    }

    public void deleteGameWorld(int gameId, World world) {
        unloadAndDeleteWorld(world);

        if (!gameWorlds.remove(gameId, world))
            throw new RuntimeException("Could not remove world '" + world.getName() + "' from gameWorlds");
    }

    public void deleteAllGameWorlds() {
        this.gameWorlds.forEach((_, world) -> unloadAndDeleteWorld(world));
        this.gameWorlds.clear();
    }

    private static void unloadAndDeleteWorld(World world) {
        if (Bukkit.getWorlds().contains(world) && !Bukkit.unloadWorld(world, false))
            throw new RuntimeException("Could not unload world '" + world.getName() + "'");

        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
