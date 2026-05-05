package dev.zenqrt.clownchase.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.paper.profile.MutablePropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;
import java.util.UUID;

public final class Clown extends Husk {

    private static final ResolvableProfile HEAD_PROFILE;

    static {
        MutablePropertyMap properties = new MutablePropertyMap();
        properties.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ1ZGVjMzY3MjRiZmQzOGNlOWU2N2RhNWRmZDA2NWQ1YjM1OGExOWU3MDNjN2I5M2JmYzhkZWE0NzY5YTEzYWQiCiAgICB9CiAgfQp9", ""));

        HEAD_PROFILE = ResolvableProfile.createResolved(new GameProfile(UUID.randomUUID(), "", properties));
    }


    public Clown(Player target, Level level) {
        super(EntityType.HUSK, level);

        this.setTarget(target, EntityTargetEvent.TargetReason.CUSTOM);
        this.setInvulnerable(true);

        setupEquipment();
    }

    private void setupEquipment() {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        head.set(DataComponents.PROFILE, HEAD_PROFILE);

        ItemStack chest = new ItemStack(Items.LEATHER_CHESTPLATE);
        chest.set(DataComponents.DYED_COLOR, new DyedItemColor(0xFFFFFF));

        ItemStack legs = new ItemStack(Items.LEATHER_LEGGINGS);
        legs.set(DataComponents.DYED_COLOR, new DyedItemColor(0xc75d5d));

        ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
        boots.set(DataComponents.DYED_COLOR, new DyedItemColor(0x614027));

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
