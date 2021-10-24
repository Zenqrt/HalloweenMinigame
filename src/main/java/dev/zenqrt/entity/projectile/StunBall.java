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

public class StunBall extends PowerUpProjectile {

    private static final ParticleEmitter COLLISION_PARTICLE_EMITTER = new ParticleEmitter(Particle.FIREWORK, Vec.ZERO, 0.5f, 50, true);

    public StunBall(@Nullable Player shooter) {
        super("Stun Ball", shooter, EntityType.SNOWBALL);
    }

    @Override
    public Potion getWeirdoPotionMan() {
        return new Potion(PotionEffect.SLOWNESS, (byte) 100, 10);
    }

    @Override
    public Sound getLocalHitSound() {
        return Sound.sound(SoundEvent.BLOCK_ANVIL_LAND, Sound.Source.PLAYER, 1, 0.5f);
    }

    @Override
    public Sound getGlobalHitSound() {
        return Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_BLAST, Sound.Source.PLAYER, 1, 0.5f);
    }

    @Override
    public ParticleEmitter getCollisionParticleEmitter() {
        return COLLISION_PARTICLE_EMITTER;
    }
}
