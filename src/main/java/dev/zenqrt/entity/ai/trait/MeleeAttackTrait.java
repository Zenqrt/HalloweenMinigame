package dev.zenqrt.entity.ai.trait;

import dev.zenqrt.entity.ai.PlayerEntityTrait;
import dev.zenqrt.entity.player.PlayerEntity;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class MeleeAttackTrait extends PlayerEntityTrait {

    private final Cooldown cooldown;
    private long lastHit;
    private final double range;
    private final Duration delay;
    private boolean stop;
    private Entity cachedTarget;

    public MeleeAttackTrait(@NotNull PlayerEntity playerEntity, double range, int delay, @NotNull TemporalUnit timeUnit) {
        this(playerEntity, range, Duration.of(delay, timeUnit));
    }

    public MeleeAttackTrait(@NotNull PlayerEntity playerEntity, double range, Duration delay) {
        super(playerEntity);
        this.cooldown = new Cooldown(Duration.of(5L, TimeUnit.SERVER_TICK));
        this.range = range;
        this.delay = delay;
    }

    @NotNull
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    public boolean shouldStart() {
        this.cachedTarget = this.findTarget();
        return this.cachedTarget != null;
    }

    public void start() {
        var targetPosition = this.cachedTarget.getPosition();
        this.playerEntity.getNavigator().setPathTo(targetPosition);
    }

    public void tick(long time) {
        Entity target;
        if (this.cachedTarget != null) {
            target = this.cachedTarget;
            this.cachedTarget = null;
        } else {
            target = this.findTarget();
        }

        this.stop = target == null || target.isRemoved();
        if (!this.stop) {
            if (this.playerEntity.getDistance(target) <= this.range) {
                if (!Cooldown.hasCooldown(time, this.lastHit, this.delay)) {
                    this.playerEntity.attack(target, true);
                    this.lastHit = time;
                }

                return;
            }

            var navigator = this.playerEntity.getNavigator();
            var pathPosition = navigator.getPathPosition();
            var targetPosition = target.getPosition();
            if ((pathPosition == null || !pathPosition.samePoint(targetPosition)) && this.cooldown.isReady(time)) {
                this.cooldown.refreshLastUpdate(time);
                navigator.setPathTo(targetPosition);
            }
        }

    }

    public boolean shouldEnd() {
        return this.stop;
    }

    public void end() {
        this.playerEntity.getNavigator().setPathTo(null);
    }
}
