package dev.zenqrt.world.block.handlers;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class DamageTrapHandler implements TrapBlockHandler {

    @Override
    public void onTrap(Touch touch) {
        if(!(touch.getTouching() instanceof Player player)) return;
        player.damage(DamageType.ON_FIRE, 2);
    }

    @Override
    public String getDisplayName() {
        return "Damage Trap";
    }

    @Override
    public Sound getCatchSound() {
        return Sound.sound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE, Sound.Source.PLAYER, 1, 1.25F);
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("halloween:damage_trap");
    }
}
