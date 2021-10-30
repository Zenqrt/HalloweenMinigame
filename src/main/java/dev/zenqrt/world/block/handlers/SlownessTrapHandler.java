package dev.zenqrt.world.block.handlers;

import dev.zenqrt.potion.SlownessEffectHandler;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;

public class SlownessTrapHandler implements TrapBlockHandler {

    @Override
    public void onTrap(Touch touch) {
        if(!(touch.getTouching() instanceof Player player)) return;
        MinestomServer.getPotionEffectManager().applyEffect(player, new Potion(PotionEffect.SLOWNESS, (byte) 4,  100), new SlownessEffectHandler(player));
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
