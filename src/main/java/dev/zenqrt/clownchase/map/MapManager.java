package dev.zenqrt.clownchase.map;

import dev.zenqrt.clownchase.utils.file.MyFileUtils;
import dev.zenqrt.clownchase.world.generator.VoidGenerator;
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

public final class MapManager {

    private static final Path WORLDS_DIR = Bukkit.getWorldContainer().toPath().resolve("world/dimensions/minecraft");
    private final Map<Integer, World> gameWorlds = new HashMap<>();

    public World createGameWorld(int gameId) {
        String worldName = "clown-chase_" + gameId;

        benchmarkStart();
        try {
            MyFileUtils.copyResourceFolder("worlds/empty", WORLDS_DIR.resolve(worldName));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        benchmarkEnd("Copying");


        benchmarkStart();

        World world = WorldCreator.name(worldName)
                .generator(new VoidGenerator())
                .forcedSpawnPosition(Position.BLOCK_ZERO, 0, 0)
                .createWorld();

        if (world == null)
            throw new NullPointerException("world");

        world.setAutoSave(false);
        world.setGameRule(GameRules.SPAWN_MOBS, false);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.ADVANCE_WEATHER, false);
        world.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRules.FALL_DAMAGE, false);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);

        benchmarkEnd("Creating world");

        this.gameWorlds.put(gameId, world);

        return world;
    }

    static long start, end;

    public static void benchmarkStart() {
        start = System.currentTimeMillis();
    }

    public static void benchmarkEnd(String task) {
        end = System.currentTimeMillis();
        System.out.println(task + " took " + (end - start) + "ms");
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
