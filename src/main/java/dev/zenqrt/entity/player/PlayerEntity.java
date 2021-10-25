package dev.zenqrt.entity.player;

import dev.zenqrt.entity.ai.PlayerEntityAI;
import dev.zenqrt.entity.ai.PlayerEntityTraitGroup;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class PlayerEntity extends FakePlayer implements PlayerEntityAI {

    private final Set<PlayerEntityTraitGroup> traitGroups;

    private Entity target;

    public PlayerEntity(@NotNull UUID uuid, @NotNull String username, @NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(uuid, username, option, spawnCallback);

        this.traitGroups = new CopyOnWriteArraySet<>();
    }

    @Override
    public void update(long time) {
        this.aiTick(time);
        super.update(time);
    }

    public Set<PlayerEntityTraitGroup> getTraitGroups() {
        return traitGroups;
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    @Nullable
    public Entity getTarget() {
        return target;
    }

    public void attack(@NotNull Entity target, boolean swingHand) {
        if (swingHand) {
            this.swingMainHand();
        }

        var attackEvent = new EntityAttackEvent(this, target);
        EventDispatcher.call(attackEvent);
    }

    public void attack(@NotNull Entity target) {
        this.attack(target, false);
    }
}
