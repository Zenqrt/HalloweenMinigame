package dev.zenqrt.server.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.server.MojangService;
import dev.zenqrt.utils.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.mojang.MojangUtils;

import java.util.Objects;
import java.util.UUID;

@Node(name = "server-events", event = PlayerEvent.class)
public class ServerEvents {

    private final InstanceContainer instanceContainer;
    private final PlainTextComponentSerializer plainText;

    public ServerEvents(InstanceContainer instanceContainer) {
        this.instanceContainer = instanceContainer;
        this.plainText = PlainTextComponentSerializer.plainText();
    }

    @Listen
    public void onPlayerLogin(PlayerLoginEvent event) {
        final var player = event.getPlayer();

        event.setSpawningInstance(instanceContainer);
        player.setRespawnPoint(WorldUtils.getInstanceSpawnpoint(instanceContainer));
        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlying(true);
        player.setUuid(Objects.requireNonNullElse(MojangService.retrieveUuid(player.getUsername()), UUID.randomUUID()));

        if(plainText.serialize(player.getName()).equals("Walmqrt")) {
            player.addPermission(new Permission("events.command.spawnclown"));
            System.out.println("has: " + player.hasPermission("events.command.spawnclown"));
        }
    }

}
