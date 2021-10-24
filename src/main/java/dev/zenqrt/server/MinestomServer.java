package dev.zenqrt.server;

import co.aikar.commands.MinestomCommandManager;
import com.github.christian162.EventAPI;
import dev.zenqrt.commands.CreateMazeCommand;
import dev.zenqrt.commands.GiveCommand;
import dev.zenqrt.commands.SpawnClownCommand;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.listeners.GameEvents;
import dev.zenqrt.world.generator.StoneFlatGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;

public class MinestomServer {

    private static MinestomCommandManager commandManager;
    private static EventAPI eventManager;
    private static GameManager gameManager;
    private static InstanceContainer instanceContainer;

    public static void main(String[] args) {
        var server = MinecraftServer.init();
        MojangAuth.init();
        OptifineSupport.enable();

        server.start("0.0.0.0", 25565);
        MinecraftServer.setBrandName("boo");

        instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer();
        instanceContainer.setChunkGenerator(new StoneFlatGenerator());

        commandManager = new MinestomCommandManager();
        commandManager.registerCommand(new CreateMazeCommand());
        commandManager.registerCommand(new SpawnClownCommand());
        commandManager.registerCommand(new GiveCommand());

        gameManager = new GameManager();

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
