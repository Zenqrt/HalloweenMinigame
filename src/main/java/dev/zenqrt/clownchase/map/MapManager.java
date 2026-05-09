package dev.zenqrt.clownchase.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.data.serializers.BiomeSerializer;
import dev.zenqrt.clownchase.data.serializers.MazeThemeSerializer;
import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import dev.zenqrt.clownchase.utils.world.GameWorldUtils;
import dev.zenqrt.clownchase.world.biome.CustomBiomeProvider;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class MapManager {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Biome.class, new BiomeSerializer())
            .registerTypeAdapter(MazeTheme.class, new MazeThemeSerializer())
            .create();

    private final Map<String, ClownChaseMap> maps = new HashMap<>();
    private final Map<Integer, World> gameWorlds = new HashMap<>();
    private final Set<UUID> gameWorldUuids = new HashSet<>();
    private final ClownChasePlugin plugin;

    public MapManager(ClownChasePlugin plugin) {
        this.plugin = plugin;
    }

    public void loadMaps(Path directoryPath) {
        try (Stream<Path> files = Files.list(directoryPath)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try (Reader reader = Files.newBufferedReader(path)) {
                            ClownChaseMap map = GSON.fromJson(reader, ClownChaseMap.class);
                            String mapId = path.getFileName().toString().replaceAll(".json", "");

                            this.registerMap(mapId, map);
                            this.plugin.getSLF4JLogger().info("Registered map '{}'", mapId);
                        } catch (IOException ex) {
                            this.plugin.getSLF4JLogger().error("Failed to load map {}", path, ex);
                        }
                    });
        } catch(IOException ex) {
            this.plugin.getSLF4JLogger().error("Failed to list files in {}", directoryPath, ex);
        }
    }

    public void registerMap(String id, ClownChaseMap map) {
        maps.put(id, map);
    }

    public boolean unregisterMap(String id) {
        return maps.remove(id) != null;
    }

    public Optional<ClownChaseMap> findMap(String id) {
        ClownChaseMap map = maps.get(id);

        return map == null ? Optional.empty() : Optional.of(map);
    }

    public Map<String, ClownChaseMap> getMaps() {
        return Collections.unmodifiableMap(maps);
    }

    public World createGameWorld(int gameId, ClownChaseMap map) {
        String worldName = "clown-chase_" + gameId;

        World world = GameWorldUtils.createWorldWithoutInit(
                WorldCreator.name(worldName)
                        .biomeProvider(new CustomBiomeProvider(map.biome()))
        );

        world.setAutoSave(false);
        world.setTime(map.time());
        world.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRules.SPAWN_MOBS, false);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.ADVANCE_WEATHER, false);
        world.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRules.FALL_DAMAGE, false);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);

        this.gameWorlds.put(gameId, world);
        this.gameWorldUuids.add(world.getUID());

        return world;
    }

    public void deleteGameWorld(int gameId, World world) {
        unloadAndDeleteWorld(world);

        if (!gameWorlds.remove(gameId, world) || !gameWorldUuids.remove(world.getUID()))
            throw new RuntimeException("Could not remove world '" + world.getName() + "' from gameWorlds");
    }

    public void deleteAllGameWorlds() {
        this.gameWorlds.forEach((_, world) -> unloadAndDeleteWorld(world));
        this.gameWorlds.clear();

        this.gameWorldUuids.clear();
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

    public boolean isGameWorld(World world) {
        return this.gameWorldUuids.contains(world.getUID());
    }

}
