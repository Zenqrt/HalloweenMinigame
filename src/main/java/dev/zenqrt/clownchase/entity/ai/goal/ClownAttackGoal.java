package dev.zenqrt.clownchase.entity.ai.goal;

import dev.zenqrt.clownchase.entity.Clown;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

public final class ClownAttackGoal extends MeleeAttackGoal {

    private int lastHitTick;

    public ClownAttackGoal(Clown mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target) {
        if (canPerformAttack(target) && mob.tickCount - lastHitTick >= 40) {
            lastHitTick = mob.tickCount;

            resetAttackCooldown();
            mob.swing(InteractionHand.MAIN_HAND);
            mob.doHurtTarget(getServerLevel(mob), target);
        }
    }
}
