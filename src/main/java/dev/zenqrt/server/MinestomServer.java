package dev.zenqrt.server;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinestomCommandManager;
import com.github.christian162.EventAPI;
import dev.zenqrt.commands.CreateMazeCommand;
import dev.zenqrt.commands.GameCommand;
import dev.zenqrt.commands.GiveCommand;
import dev.zenqrt.commands.SpawnClownCommand;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.listeners.GameEvents;
import dev.zenqrt.potion.PotionEffectManager;
import dev.zenqrt.server.listeners.ServerEvents;
import dev.zenqrt.server.provider.MojangUuidProvider;
import dev.zenqrt.world.block.handlers.SignBlockHandler;
import dev.zenqrt.world.block.handlers.SkullBlockHandler;
import dev.zenqrt.world.block.rule.PressurePlatePlacementRule;
import dev.zenqrt.world.worlds.HalloweenLobbyWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.UuidProvider;

public class MinestomServer {

    private static MinestomCommandManager commandManager;
    private static EventAPI eventManager;
    private static GameManager gameManager;
    private static InstanceContainer instanceContainer;
    private static PotionEffectManager potionEffectManager;

    public static void main(String[] args) {
        var server = MinecraftServer.init();
        MojangAuth.init();
        OptifineSupport.enable();

        server.start("0.0.0.0", 25565);
        MinecraftServer.setBrandName("Event");

        instanceContainer = new HalloweenLobbyWorld().createInstanceContainer();
        gameManager = new GameManager();

        commandManager = new MinestomCommandManager();
        var replacements = commandManager.getCommandReplacements();
        replacements.addReplacement("cmd_perm", "events.command");

        var contexts = commandManager.getCommandContexts();
        contexts.registerContext(GamePlayer.class, c -> {
            var player = c.getPlayer();
            var gamePlayer = gameManager.findGamePlayer(player);
            if(gamePlayer == null) throw new InvalidCommandArgument("Could not find game player");
            return gamePlayer;
        });
//        c/*ommandManager.registerCommand(new CreateMazeCommand());
//        commandManager.registerCommand(new SpawnClownCommand());
        commandManager.registerCommand(new GiveCommand());
        commandManager.registerCommand(new GameCommand(gameManager));



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

        potionEffectManager = new PotionEffectManager();

        MinecraftServer.getConnectionManager().setUuidProvider(new MojangUuidProvider());

        // Event Handler
        var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        eventManager = new EventAPI(globalEventHandler);
        eventManager.register(new ServerEvents(instanceContainer));
        eventManager.register(new GameEvents(gameManager));
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

    public static PotionEffectManager getPotionEffectManager() {
        return potionEffectManager;
    }
}
