package dev.zenqrt.entity.ai;

public class PlayerEntityTraitGroupBuilder {

    private final PlayerEntityTraitGroup group = new PlayerEntityTraitGroup();

    public PlayerEntityTraitGroupBuilder addTrait(PlayerEntityTrait trait) {
        this.group.getTraits().add(trait);
        return this;
    }

    public PlayerEntityTraitGroupBuilder addTargetTrait(PlayerEntityTargetTrait targetTrait) {
        this.group.getTargetTraits().add(targetTrait);
        return this;
    }

    public PlayerEntityTraitGroup build() {
        return this.group;
    }

}
