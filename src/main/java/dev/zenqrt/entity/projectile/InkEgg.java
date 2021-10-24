package dev.zenqrt.entity.projectile;

import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class InkEgg extends PowerUpProjectile {

    private static final ParticleEmitter COLLISION_PARTICLE_EMITTER = new ParticleEmitter(Particle.SQUID_INK, Vec.ZERO, 0.1f, 50, true);

    public InkEgg(@Nullable Player shooter) {
        super("Ink Egg", shooter, EntityType.EGG);
    }

    @Override
    public Potion getWeirdoPotionMan() {
        return new Potion(PotionEffect.BLINDNESS, (byte) 100, 10);
    }

    public Sound getLocalHitSound() {
        return Sound.sound(SoundEvent.ENTITY_SQUID_SQUIRT, Sound.Source.PLAYER, 1, 0.5f);
    }

    public Sound getGlobalHitSound() {
        return Sound.sound(SoundEvent.ENTITY_SPLASH_POTION_BREAK, Sound.Source.PLAYER, 1, 0.5f);
    }

    public ParticleEmitter getCollisionParticleEmitter() {
        return COLLISION_PARTICLE_EMITTER;
    }
}
