package dev.zenqrt.clownchase.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.math.Transformation;
import dev.zenqrt.clownchase.entity.ai.goal.ClownAttackGoal;
import dev.zenqrt.clownchase.utils.entity.EntityUtils;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.profile.MutablePropertyMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

public final class Clown extends Husk {

    private static final String YOUR_CLOWN = "game.clown.self.title";
    private static final ResolvableProfile HEAD_PROFILE;

    static {
        MutablePropertyMap properties = new MutablePropertyMap();
        properties.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ1ZGVjMzY3MjRiZmQzOGNlOWU2N2RhNWRmZDA2NWQ1YjM1OGExOWU3MDNjN2I5M2JmYzhkZWE0NzY5YTEzYWQiCiAgICB9CiAgfQp9", ""));

        HEAD_PROFILE = ResolvableProfile.createResolved(new GameProfile(UUID.randomUUID(), "", properties));
    }

    private final Display.TextDisplay selfTag;
    private final Player playerTarget;

    public Clown(Player target, Level level) {
        super(EntityType.HUSK, level);

        this.playerTarget = target;

        this.selfTag = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        this.selfTag.setText(PaperAdventure.asVanilla(Component.translatable(YOUR_CLOWN, NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        this.selfTag.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        this.selfTag.setTransformation(new Transformation(new Vector3f(0, 0.25F, 0), null, null, null));
        this.selfTag.startRiding(this);

        this.setTarget(target, EntityTargetEvent.TargetReason.CUSTOM);
        this.setInvulnerable(true);

        setupEquipment();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        if (!player.getUUID().equals(playerTarget.getUUID()))
            return;

        EntityUtils.showEntity(this.selfTag, this.getX(), this.getY(), this.getZ(), player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);

        EntityUtils.hideEntity(this.selfTag, player);
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
        this.goalSelector.addGoal(0, new ClownAttackGoal(this, 1, true));
    }

    public void setSpeedModifier(float modifier) {
        AttributeInstance movementSpeed = Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED), "movementSpeed");
        movementSpeed.addOrUpdateTransientModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "clown_speed"), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return target.distanceTo(this) < 2;
    }



    @Override
    public @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_CELEBRATE;
    }

    @Override
    public float getVoicePitch() {
        return 1.5F;
    }
}
