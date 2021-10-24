package dev.zenqrt.entity.player;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerEntity extends FakePlayer {

    private Entity target;

    public PlayerEntity(@NotNull UUID uuid, @NotNull String username, @NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(uuid, username, option, spawnCallback);

    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    @Nullable
    public Entity getTarget() {
        return target;
    }
}
