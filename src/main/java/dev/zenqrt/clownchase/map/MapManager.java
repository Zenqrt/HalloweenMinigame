package dev.zenqrt.clownchase.map;

import dev.zenqrt.clownchase.world.generator.VoidGenerator;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class MapManager {

    private final Map<Integer, World> gameWorlds = new HashMap<>();

    public World createGameWorld(int gameId) {
        System.out.println(Bukkit.getWorldContainer().getAbsolutePath());
        World world = WorldCreator.name("clown-chase_" + gameId)
                .generator(new VoidGenerator())
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

        this.gameWorlds.put(gameId, world);

        return world;
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
