package dev.zenqrt.entity.player;

import dev.zenqrt.entity.ai.PlayerEntityAI;
import dev.zenqrt.entity.ai.PlayerEntityTraitGroup;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class PlayerEntity extends FakePlayer implements PlayerEntityAI {

    private final Set<PlayerEntityTraitGroup> traitGroups;
    private final Set<Player> skinLoaded;

    private Entity target;

    public PlayerEntity(@NotNull UUID uuid, @NotNull String username, @NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(uuid, username, option, spawnCallback);

        this.traitGroups = new CopyOnWriteArraySet<>();
        this.skinLoaded = new HashSet<>();
        this.metadata.setIndex(17, Metadata.Byte((byte) 0x40));
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

    @Override
    protected boolean addViewer0(@NotNull Player player) {
        var success = super.addViewer0(player);
        if(!success) return false;

        if(!skinLoaded.contains(player)) {
            MinecraftServer.getSchedulerManager().buildTask(() -> updateSkin(Set.of(player))).delay(1, TimeUnit.SECOND).schedule();
        }

        return true;
    }

    @Override
    protected boolean removeViewer0(@NotNull Player player) {
        var success = super.removeViewer0(player);
        if(!success) return false;

        this.skinLoaded.remove(player);

        return true;
    }

    @Override
    public synchronized void setSkin(@Nullable PlayerSkin skin) {
        super.setSkin(skin);
        this.skinLoaded.addAll(getViewers());
    }

    public synchronized void updateSkin(Set<Player> viewers) {
        if (this.instance != null) {
            viewers.forEach((player) -> this.showPlayer(player.getPlayerConnection()));
            this.skinLoaded.addAll(viewers);
        }
    }
}
