package dev.zenqrt.entity.monster;

import dev.zenqrt.entity.ai.PlayerEntityTraitGroupBuilder;
import dev.zenqrt.entity.ai.trait.MeleeAttackTrait;
import dev.zenqrt.entity.other.FollowingHologram;
import dev.zenqrt.entity.other.HologramTag;
import dev.zenqrt.entity.player.PlayerEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.TemporalUnit;
import java.util.UUID;
import java.util.function.Consumer;

public class KillerClown extends PlayerEntity implements HologramTag {

    private FollowingHologram hologramTag;
    private boolean attacking;

    public KillerClown(@NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(UUID.randomUUID(), "murder_clown", option, spawnCallback);
        this.addTraitGroup(
                new PlayerEntityTraitGroupBuilder()
                .addTrait(new ClownMeleeAttackTrait(1, 1, TimeUnit.SECOND))
                .build()
        );
        this.setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ1ZGVjMzY3MjRiZmQzOGNlOWU2N2RhNWRmZDA2NWQ1YjM1OGExOWU3MDNjN2I5M2JmYzhkZWE0NzY5YTEzYWQiCiAgICB9CiAgfQp9",
                "qoV7XN4TpAKLIO4bpdfWxgRh0xAvNOWgRjHK2IWJ4h6mbBbZF7R6bcKBOJI1U4c926ZBon6VnZ3NYIPVA8yRmcC5hGSeuI0VdpTXg41OibowZ4DQUzuwMU25vTzjdnH2j6MC103LcsuCNyoQElAJHYS3P03JITd0jrtJxhJcGOfWELy9h4slSQhs2bJxF2JycygDfef2WkWggzdnZa9CF/QcD8ygPx33MTGT0pamLBzLOV3kVtIK4jo26STYDDF0a2bR8VKu9iFOwlt/MZDUJO5WPNGbNbv0vph6njAtct3exkprsj97CPyemTyrlHsnHaJQpcnHc9LQKM3rOwirqM4inG/8v4QBSx9pymcwCidb7YH7/12M+hPi2mlyYHQpOJLVjigDS68pJw98PBxNjY6sfnUcHFxUNLQXDLGCKpOS/jadOdQgtoRqWEY1tXwPYghs5Q3ZzFdd944W6LsXtQBbOa+niOxo9QTNS6+Lo5DVBKto7XhmdNhPa4fj/RqzVjgXPdqY7/CX+9TWg6+qRpZmDs+XH9dhKKPHneu2zXjoHXGpxFod+EUyFnFBfI/HgJGSBSPF+O575MYwZAolhirUVvxLeJv0FbWM2365O37dbnDGEryUVIBJrryNOMI9peFA+m+xIqHxfqO09V2QHVLZdgOrL3eZuLsABb69QIU="
        ));
        this.setItemInMainHand(ItemStack.of(Material.IRON_AXE));
        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3f);
        this.attacking = false;
    }

    @Override
    public void spawn() {
        this.hologramTag = new FollowingHologram(this, Component.text("Your Clown", TextColor.color(0, 255, 153)).decorate(TextDecoration.BOLD), Vec.ZERO.withY(this.getEyeHeight() - 0.5));
    }

    @Override
    public void attack(@NotNull Entity target, boolean swingHand) {
        super.attack(target, swingHand);

        if(target instanceof LivingEntity entity) {
            var vector = this.getPosition().asVec().sub(entity.getPosition()).normalize();
            entity.damage(DamageType.fromEntity(this), 2);
            entity.takeKnockback(1, vector.x(), vector.z());
        }
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isAttacking() {
        return attacking;
    }

    @Override
    public FollowingHologram getHologramTag() {
        return hologramTag;
    }

    private class ClownMeleeAttackTrait extends MeleeAttackTrait {

        public ClownMeleeAttackTrait(double range, int delay, @NotNull TemporalUnit timeUnit) {
            super(KillerClown.this, range, delay, timeUnit);
        }

        @Override
        public boolean shouldStart() {
            return KillerClown.this.attacking && super.shouldStart();
        }

        @Override
        public @Nullable Entity findTarget() {
            return KillerClown.this.getTarget();
        }
    }
}
