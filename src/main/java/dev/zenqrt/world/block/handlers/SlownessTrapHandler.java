package dev.zenqrt.world.block.handlers;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class SlownessTrapHandler implements TrapBlockHandler {

    @Override
    public void onTrap(Touch touch) {
        if(!(touch.getTouching() instanceof Player player)) return;
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 4,  100));
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("halloween:slowness_trap");
    }

    @Override
    public String getDisplayName() {
        return "Slowness Trap";
    }

    @Override
    public Sound getCatchSound() {
        return Sound.sound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE, Sound.Source.PLAYER, 1, 1.25F);
    }
}
