package dev.zenqrt.clownchase.utils.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import dev.zenqrt.clownchase.world.generator.VoidGenerator;
import io.papermc.paper.world.PaperWorldLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.generator.BiomeProvider;

public final class GameWorldUtils {

    private GameWorldUtils() {}

    public static World createWorldWithoutPrepare(WorldCreator creator) {
        String name = creator.name();
        MinecraftServer server = MinecraftServer.getServer();

        BiomeProvider biomeProvider = creator.biomeProvider();

        if (biomeProvider == null)
            biomeProvider = ((CraftServer) Bukkit.getServer()).getBiomeProvider(name);

        ResourceKey<Level> dimensionKey = PaperWorldLoader.dimensionKey(creator.key());
        ResourceKey<LevelStem> actualDimension = switch (creator.environment()) {
            case NORMAL -> LevelStem.OVERWORLD;
            case NETHER -> LevelStem.NETHER;
            case THE_END -> LevelStem.END;
            default -> throw new IllegalArgumentException("Illegal dimension (" + creator.environment().name() + ")");
        };

        WorldLoader.DataLoadContext context = server.worldLoaderContext;
        PaperWorldLoader.LoadedWorldData loadedWorldData = PaperWorldLoader.loadWorldData(server, dimensionKey, name);
        PrimaryLevelData primaryLevelData = (PrimaryLevelData) server.getWorldData();

        RegistryAccess.Frozen registryAccess = context.datapackDimensions();
        Registry<LevelStem> contextLevelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
        WorldGenSettings worldGenSettings = LevelStorageSource.readExistingSavedData(server.storageSource, dimensionKey, server.registryAccess(), WorldGenSettings.TYPE)
                .result()
                .orElse(null);

        if (worldGenSettings == null) {
            WorldOptions worldOptions = new WorldOptions(0, false, false);
            DedicatedServerProperties.WorldDimensionData properties = new DedicatedServerProperties.WorldDimensionData(
                    new JsonObject(),
                    "normal"
            );
            WorldDimensions worldDimensions = properties.create(context.datapackWorldgen());
            WorldDimensions.Complete complete = worldDimensions.bake(contextLevelStemRegistry);

            if (complete.dimensions().getValue(actualDimension) == null)
                throw new IllegalStateException("Missing generated level stem " + actualDimension + " for world " + name);

            worldGenSettings = new WorldGenSettings(worldOptions, worldDimensions);
        }

        long biomeZoomSeed = BiomeManager.obfuscateSeed(worldGenSettings.options().seed());
        LevelStem customStem = worldGenSettings.dimensions()
                .get(actualDimension)
                .orElseGet(() -> {
                    LevelStem stem = contextLevelStemRegistry.getValue(actualDimension);

                    if (stem == null)
                        throw new IllegalStateException("Missing level stem for world " + name + " using key " + actualDimension);

                    return stem;
                });

        SavedDataStorage savedDataStorage = new SavedDataStorage(
                server.storageSource.getDimensionPath(dimensionKey).resolve(LevelResource.DATA.id()),
                server.getFixerUpper(),
                server.registryAccess()
        );
        savedDataStorage.set(WorldGenSettings.TYPE, new WorldGenSettings(worldGenSettings.options(), worldGenSettings.dimensions()));

        ServerLevel level = new ServerLevel(
                server,
                server.executor,
                server.storageSource,
                worldGenSettings,
                dimensionKey,
                customStem,
                primaryLevelData.isDebugWorld(),
                biomeZoomSeed,
                ImmutableList.of(),
                true,
                actualDimension,
                creator.environment(),
                new VoidGenerator(),
                biomeProvider,
                savedDataStorage,
                loadedWorldData
        );

        server.addLevel(level);
        level.setSpawnSettings(true);

        return level.getWorld();
    }

}
