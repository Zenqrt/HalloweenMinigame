package dev.zenqrt.entity.ai;

import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

public class PlayerEntityTraitGroup {

    private PlayerEntityTrait currentTrait;
    private final List<PlayerEntityTrait> traits = new PlayerEntityTraitArrayList();
    private final List<PlayerEntityTargetTrait> targetTraits = new ArrayList<>();

    public List<PlayerEntityTrait> getTraits() {
        return traits;
    }

    public List<PlayerEntityTargetTrait> getTargetTraits() {
        return targetTraits;
    }

    @Nullable
    public PlayerEntityTrait getCurrentTrait() {
        return currentTrait;
    }

    public void setCurrentTrait(@Nullable PlayerEntityTrait trait) {
        Check.argCondition(trait != null && trait.getTraitGroup() != this, "Tried to set entity trait attached to another trait group!");
        this.currentTrait = trait;
    }

    public void tick(long time) {
        var currentTraitSelector = this.getCurrentTrait();
        if (currentTraitSelector != null && currentTraitSelector.shouldEnd()) {
            currentTraitSelector.end();
            currentTraitSelector = null;
            this.setCurrentTrait(null);
        }

        for (PlayerEntityTrait trait : this.getTraits()) {
            if (trait == currentTraitSelector) {
                break;
            }

            if (trait.shouldStart()) {
                if (currentTraitSelector != null) {
                    currentTraitSelector.end();
                }

                currentTraitSelector = trait;
                this.setCurrentTrait(trait);
                trait.start();
                break;
            }
        }

        if (currentTraitSelector != null) {
            currentTraitSelector.tick(time);
        }
    }

    private class PlayerEntityTraitArrayList extends ArrayList<PlayerEntityTrait> {

        public PlayerEntityTrait set(int index, PlayerEntityTrait element) {
            element.setTraitGroup(PlayerEntityTraitGroup.this);
            return super.set(index, element);
        }

        public boolean add(PlayerEntityTrait element) {
            element.setTraitGroup(PlayerEntityTraitGroup.this);
            return super.add(element);
        }

        public void add(int index, PlayerEntityTrait element) {
            element.setTraitGroup(PlayerEntityTraitGroup.this);
            super.add(index, element);
        }

        public boolean addAll(Collection<? extends PlayerEntityTrait> c) {
            c.forEach((trait) -> trait.setTraitGroup(PlayerEntityTraitGroup.this));
            return super.addAll(c);
        }

        public boolean addAll(int index, Collection<? extends PlayerEntityTrait> c) {
            c.forEach((trait) -> trait.setTraitGroup(PlayerEntityTraitGroup.this));
            return super.addAll(index, c);
        }

        public void replaceAll(UnaryOperator<PlayerEntityTrait> operator) {
            super.replaceAll((trait) -> {
                trait = operator.apply(trait);
                trait.setTraitGroup(PlayerEntityTraitGroup.this);
                return trait;
            });
        }

    }
}
