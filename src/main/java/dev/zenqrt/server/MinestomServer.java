package dev.zenqrt.server;

import co.aikar.commands.MinestomCommandManager;
import com.github.christian162.EventAPI;
import dev.zenqrt.commands.CreateMazeCommand;
import dev.zenqrt.commands.GiveCommand;
import dev.zenqrt.commands.SpawnClownCommand;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.server.listeners.ServerEvents;
import dev.zenqrt.world.block.handlers.SignBlockHandler;
import dev.zenqrt.world.block.handlers.SkullBlockHandler;
import dev.zenqrt.world.block.rule.PressurePlatePlacementRule;
import dev.zenqrt.world.worlds.HalloweenLobbyWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

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
        MinecraftServer.setBrandName("Event");

        instanceContainer = new HalloweenLobbyWorld().createInstanceContainer();

        commandManager = new MinestomCommandManager();
        var replacements = commandManager.getCommandReplacements();
        replacements.addReplacement("cmd_perm", "events.command");
        commandManager.registerCommand(new CreateMazeCommand());
        commandManager.registerCommand(new SpawnClownCommand());
        commandManager.registerCommand(new GiveCommand());

        gameManager = new GameManager();

        var blockManager = MinecraftServer.getBlockManager();
        blockManager.registerHandler("minecraft:skull", SkullBlockHandler::new);
        blockManager.registerHandler("minecraft:sign", SignBlockHandler::new);
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.STONE_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.OAK_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.BIRCH_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.SPRUCE_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.DARK_OAK_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.ACACIA_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.JUNGLE_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.CRIMSON_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.WARPED_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.POLISHED_BLACKSTONE_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.LIGHT_WEIGHTED_PRESSURE_PLATE));
        blockManager.registerBlockPlacementRule(new PressurePlatePlacementRule(Block.HEAVY_WEIGHTED_PRESSURE_PLATE));


        // Event Handler
        var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        eventManager = new EventAPI(globalEventHandler);
        eventManager.register(new ServerEvents(instanceContainer));
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
