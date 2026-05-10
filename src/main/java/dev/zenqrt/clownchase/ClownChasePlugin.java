package dev.zenqrt.clownchase;

import dev.zenqrt.clownchase.commands.ClownChaseCommand;
import dev.zenqrt.clownchase.commands.MazeCommand;
import dev.zenqrt.clownchase.event.listeners.GameWorldListeners;
import dev.zenqrt.clownchase.event.listeners.GameplayListeners;
import dev.zenqrt.clownchase.event.listeners.PlayerActivityListeners;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.map.MapManager;
import dev.zenqrt.clownchase.utils.player.PlayerUtils;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;

public final class ClownChasePlugin extends JavaPlugin {

    private static MapManager mapManager;

    @Override
    public void onEnable() {
        registerTranslations(ClownChasePlugin.class.getClassLoader().getResourceAsStream("lang/en_us.lang"));

        mapManager = new MapManager(this, getGameMapsDirectory());
        mapManager.loadMaps();

        GameManager gameManager = new GameManager(this, mapManager);

        Bukkit.getPluginManager().registerEvents(new PlayerActivityListeners(this, gameManager), this);
        Bukkit.getPluginManager().registerEvents(new GameplayListeners(), this);
        Bukkit.getPluginManager().registerEvents(new GameWorldListeners(mapManager), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            ClownChaseCommand.register(commands.registrar(), gameManager, mapManager);
            MazeCommand.register(commands.registrar());
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerUtils.forceRemove(player, EntityRemoveEvent.Cause.UNLOAD));
        mapManager.deleteAllGameWorlds();
    }

    public Location getLobbySpawn() {
        return getServer().getRespawnWorld().getSpawnLocation().toCenterLocation();
    }

    public Path getGameMapsDirectory() {
        return getDataFolder().toPath().resolve("maps");
    }

    private static void registerTranslations(InputStream langStream) {
        final TranslationStore<MessageFormat> store = TranslationStore.messageFormat(Key.key("clownchase:server_translations"));

        try (InputStreamReader reader = new InputStreamReader(langStream)) {
            reader.readAllLines().stream()
                    .map(line -> line.split("="))
                    .forEach(str -> store.register(str[0].trim(), Locale.US, new MessageFormat(str[1].trim(), Locale.US)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GlobalTranslator.translator().addSource(store);
    }
}
