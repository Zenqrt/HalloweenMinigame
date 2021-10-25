package dev.zenqrt.entity.ai;

import dev.zenqrt.entity.player.PlayerEntity;
import net.minestom.server.entity.Entity;

public abstract class PlayerEntityTargetTrait {

    protected final PlayerEntity playerEntity;

    public PlayerEntityTargetTrait(PlayerEntity entity) {
        this.playerEntity = entity;
    }

    public abstract Entity findTarget();

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }
}
