package dev.zenqrt.clownchase;

import dev.zenqrt.clownchase.commands.GameCommand;
import dev.zenqrt.clownchase.commands.MazeCommand;
import dev.zenqrt.clownchase.event.listeners.PlayerActivityListeners;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.game.GameSettings;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Locale;

public final class ClownChasePlugin extends JavaPlugin {

    private static GameManager gameManager;

    @Override
    public void onEnable() {
        registerTranslations(ClownChasePlugin.class.getClassLoader().getResourceAsStream("lang/en_us.lang"));

        gameManager = new GameManager(this);
        gameManager.createGame(new GameSettings(6, 12, 300)).start();

        Bukkit.getPluginManager().registerEvents(new PlayerActivityListeners(this, gameManager), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            GameCommand.register(commands.registrar(), gameManager);
            MazeCommand.register(commands.registrar());
        });

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
