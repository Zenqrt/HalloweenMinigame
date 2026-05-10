package dev.zenqrt.clownchase.entity.ai.goal;

import dev.zenqrt.clownchase.entity.Clown;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public final class ClownAttackGoal extends MeleeAttackGoal {

    public ClownAttackGoal(Clown mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    protected int getAttackInterval() {
        return 40;
    }
}
