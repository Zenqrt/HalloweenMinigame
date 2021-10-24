package dev.zenqrt.server;

import co.aikar.commands.MinestomCommandManager;
import com.github.christian162.EventAPI;
import dev.zenqrt.commands.CreateMazeCommand;
import dev.zenqrt.commands.GiveCommand;
import dev.zenqrt.commands.SpawnClownCommand;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.item.listeners.EntityEvents;
import dev.zenqrt.item.listeners.PacketEvents;
import dev.zenqrt.world.generator.StoneFlatGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.client.play.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;

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

//        MinecraftServer.getPacketListenerManager().setListener(ClientUseItemPacket.class, (packet, player) -> System.out.println("just seeing if this works packet"));

        // Event Handler
        var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        eventManager = new EventAPI(globalEventHandler);
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(getInstanceContainer());
            player.setRespawnPoint(new Pos(0, 42, 0));
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlying(true);
        });
        eventManager.register(new EntityEvents());
        // this is what i did
        gameManager = new GameManager();

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
