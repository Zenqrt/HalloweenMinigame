package dev.zenqrt.clownchase.entity;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;

public final class Clown extends Zombie {

//    private static final ResolvableProfile HEAD_PROFILE = ResolvableProfile.


    public Clown(Player target, Level level) {
        super(EntityType.ZOMBIE, level);

        this.setTarget(target, EntityTargetEvent.TargetReason.CUSTOM);
        this.setInvulnerable(true);

        setupEquipment();
    }

    private void setupEquipment() {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
//        head.set(DataComponents.PROFILE, HEAD_PROFILE);

        ItemStack chest = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack legs = new ItemStack(Items.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);

        this.equipment.set(EquipmentSlot.HEAD, head);
        this.equipment.set(EquipmentSlot.CHEST, chest);
        this.equipment.set(EquipmentSlot.LEGS, legs);
        this.equipment.set(EquipmentSlot.FEET, boots);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1, true));
    }

    public void setSpeedModifier(float modifier) {
        AttributeInstance movementSpeed = Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED), "movementSpeed");
        movementSpeed.addOrUpdateTransientModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "clown_speed"), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
}
