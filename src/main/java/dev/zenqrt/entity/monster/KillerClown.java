package dev.zenqrt.entity.monster;

import dev.zenqrt.entity.ai.PlayerEntityTraitGroupBuilder;
import dev.zenqrt.entity.ai.trait.MeleeAttackTrait;
import dev.zenqrt.entity.other.FollowingHologram;
import dev.zenqrt.entity.other.HologramTag;
import dev.zenqrt.entity.player.PlayerEntity;
import dev.zenqrt.utils.entity.EntityUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class KillerClown extends PlayerEntity implements HologramTag {

    private FollowingHologram hologramTag;
    private boolean attacking;

    @SuppressWarnings("all")
    public KillerClown(@NotNull FakePlayerOption option, Player target, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(UUID.randomUUID(), "murder_clown", option, spawnCallback);
        this.getNavigator().getPathingEntity().setSearchRange(100);
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
        enableSkinLayers();
        this.setTarget(target);
    }

    @Override
    public void spawn() {
        super.spawn();
        this.hologramTag = new FollowingHologram(this, Component.text("Your Clown", TextColor.color(0, 255, 153)).decorate(TextDecoration.BOLD), Vec.ZERO.withY(this.getEyeHeight() - 0.5));
    }

    @Override
    public void update(long time) {
        super.update(time);

        if(getTarget() instanceof Player player) {
            if(hologramTag != null) {
                this.hologramTag.getEntity().addViewer(player);
            }

            entityMeta.setNotifyAboutChanges(false);
            entityMeta.setHasGlowingEffect(true);
            var packet = getMetadataPacket();
            player.getPlayerConnection().sendPacket(packet);
            entityMeta.setHasGlowingEffect(false);
        }

        if(hologramTag != null) {
            var remove = new ArrayList<Player>();
            for(var player : hologramTag.getViewers()) {
                if(player != getTarget()) {
                    remove.add(player);
                }
            }
            remove.forEach(hologramTag::removeViewer);
        }
    }

    @Override
    public void attack(@NotNull Entity target, boolean swingHand) {
        super.attack(target, swingHand);

        if(target instanceof LivingEntity entity) {
            if(EntityUtils.hasActiveEffect(entity, PotionEffect.RESISTANCE)) {
                var pos = entity.getPosition();
                instance.playSound(Sound.sound(SoundEvent.BLOCK_LAVA_EXTINGUISH, Sound.Source.PLAYER, 1, 1), pos.x(), pos.y(), pos.z());
                return;
            }

            var vector = this.getPosition().asVec().sub(entity.getPosition()).normalize().mul(-10);
            entity.damage(DamageType.fromEntity(this), 2);
            entity.setVelocity(vector.withY(10));
//            entity.takeKnockback(1, vector.x(), vector.z());
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
        public boolean shouldEnd() {
            return !KillerClown.this.attacking || super.shouldEnd();
        }

        @Override
        public @Nullable Entity findTarget() {
            return KillerClown.this.getTarget();
        }
    }
}
