package dev.zenqrt.server;

import co.aikar.commands.MinestomCommandManager;
import com.github.christian162.EventAPI;
import dev.zenqrt.commands.CreateMazeCommand;
import dev.zenqrt.commands.GiveCommand;
import dev.zenqrt.commands.SpawnClownCommand;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.listeners.GameEvents;
import dev.zenqrt.world.block.SignBlockHandler;
import dev.zenqrt.world.block.SkullBlockHandler;
import dev.zenqrt.world.worlds.HalloweenLobbyWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.BlockManager;

import java.net.URISyntaxException;

public class MinestomServer {

    private static MinestomCommandManager commandManager;
    private static EventAPI eventManager;
    private static GameManager gameManager;
    private static InstanceContainer instanceContainer;

    public static void main(String[] args) throws URISyntaxException {
        var server = MinecraftServer.init();
        MojangAuth.init();
        OptifineSupport.enable();

        server.start("0.0.0.0", 25565);
        MinecraftServer.setBrandName("boo");

        instanceContainer = new HalloweenLobbyWorld().createInstanceContainer();

        commandManager = new MinestomCommandManager();
        commandManager.registerCommand(new CreateMazeCommand());
        commandManager.registerCommand(new SpawnClownCommand());
        commandManager.registerCommand(new GiveCommand());

        gameManager = new GameManager();

        var blockManager = MinecraftServer.getBlockManager();
        blockManager.registerHandler("minecraft:skull", SkullBlockHandler::new);
        blockManager.registerHandler("minecraft:sign", SignBlockHandler::new);


        // Event Handler
        var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        eventManager = new EventAPI(globalEventHandler);
        eventManager.register(new GameEvents(gameManager, instanceContainer));
    }

    public static MinestomCommandManager getCommandManager() {
        return commandManager;
    }

    public static EventAPI getEventManager() {
        return eventManager;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static InstanceContainer getInstanceContainer() {
        return instanceContainer;
    }
}
