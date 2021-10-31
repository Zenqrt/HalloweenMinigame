package dev.zenqrt.server.provider;

import dev.zenqrt.server.MojangService;
import net.minestom.server.network.UuidProvider;
import net.minestom.server.network.player.PlayerConnection;

import java.util.Objects;
import java.util.UUID;

public class MojangUuidProvider implements UuidProvider {

    @Override
    public UUID provide(PlayerConnection playerConnection, String s) {
        return Objects.requireNonNullElse(MojangService.retrieveUuid(playerConnection.getIdentifier()), UUID.randomUUID());
    }
}
