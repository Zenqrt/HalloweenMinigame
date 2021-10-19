package dev.zenqrt.server;

import co.aikar.commands.MinestomCommandManager;
import com.github.christian162.EventAPI;
import com.github.christian162.EventAPIOptions;
import dev.zenqrt.world.generator.StoneFlatGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;

public class MinestomServer {

    private static MinestomCommandManager commandManager;
    private static EventAPI eventManager;
    private static InstanceContainer instanceContainer;

    public static void main(String[] args) {
        MojangAuth.init();
        OptifineSupport.enable();

        instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer();
        instanceContainer.setChunkGenerator(new StoneFlatGenerator());

        commandManager = new MinestomCommandManager();

        // Event Handler
        var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        var eventAPIOptions = new EventAPIOptions();
        eventAPIOptions.setDefaultParentNode(globalEventHandler);
        eventAPIOptions.setRegisterInvalidChildren(false);
        eventManager = new EventAPI(eventAPIOptions);
    }

    public static MinestomCommandManager getCommandManager() {
        return commandManager;
    }

    public static EventAPI getEventManager() {
        return eventManager;
    }

    public static InstanceContainer getInstanceContainer() {
        return instanceContainer;
    }
}
