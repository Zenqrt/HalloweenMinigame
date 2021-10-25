package dev.zenqrt.entity.ai;

import java.util.Collection;
import java.util.List;

public interface PlayerEntityAI {

    Collection<PlayerEntityTraitGroup> getTraitGroups();

    default void addTraitGroup(PlayerEntityTraitGroup group) {
        this.getTraitGroups().add(group);
    }

    default void addTraitGroup(List<PlayerEntityTrait> traits, List<PlayerEntityTargetTrait> targetTraits) {
        var group = new PlayerEntityTraitGroup();
        group.getTraits().addAll(traits);
        group.getTargetTraits().addAll(targetTraits);
        this.addTraitGroup(group);
    }

    default void aiTick(long time) {
        this.getTraitGroups().forEach((group) -> {
            group.tick(time);
        });
    }

}
