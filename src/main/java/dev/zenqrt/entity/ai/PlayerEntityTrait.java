package dev.zenqrt.entity.ai;

import dev.zenqrt.entity.player.PlayerEntity;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Iterator;

public abstract class PlayerEntityTrait {

    private WeakReference<PlayerEntityTraitGroup> traitGroupWeakReference;
    protected PlayerEntity playerEntity;

    public PlayerEntityTrait(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public abstract boolean shouldStart();

    public abstract void start();

    public abstract void tick(long time);

    public abstract boolean shouldEnd();

    public abstract void end();

    @Nullable
    public Entity findTarget() {
        var group = this.getTraitGroup();
        if (group == null) {
            return null;
        } else {
            Iterator<PlayerEntityTargetTrait> var2 = group.getTargetTraits().iterator();
            Entity entity;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                var targetSelector = (PlayerEntityTargetTrait)var2.next();
                entity = targetSelector.findTarget();
            } while(entity == null);

            return entity;
        }
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void setPlayerEntity(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    void setTraitGroup(@NotNull PlayerEntityTraitGroup group) {
        this.traitGroupWeakReference = new WeakReference<>(group);
    }

    @Nullable
    protected PlayerEntityTraitGroup getTraitGroup() {
        return traitGroupWeakReference.get();
    }

}
