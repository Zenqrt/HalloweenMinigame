package dev.zenqrt.entity;

import dev.zenqrt.timer.countdown.CountdownRunnable;
import dev.zenqrt.timer.countdown.CountdownTaskBuilder;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.time.TimeUnit;

public class EffectCandy extends Candy {

    private static final ParticleEmitter CONSUME_PARTICLE_EMITTER = new ParticleEmitter(Particle.ENTITY_EFFECT, Vec.ZERO, 1, 10, true);

    private final Effect effect;

    public EffectCandy(Effect effect) {
        super(effect.color);

        this.effect = effect;
    }

    @Override
    public void consume(Player player) {
        super.consume(player);

        player.addEffect(new Potion(effect.effect, (byte) 2, 10));

        new CountdownTaskBuilder(MinecraftServer.getSchedulerManager(), new CountdownRunnable(10, timer -> {
            var pitch = (20 - timer) / 10;
            player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.PLAYER, 10, pitch), Sound.Emitter.self());
            player.sendPacketToViewersAndSelf(CONSUME_PARTICLE_EMITTER.createPacket(player.getPosition()));
        })).repeat(1, TimeUnit.SERVER_TICK);
    }

    public enum Effect {
        SPEED(PotionEffect.SPEED, Color.BLUE),
        REGENERATION(PotionEffect.REGENERATION, Color.PINK),
        INVISIBILITY(PotionEffect.INVISIBILITY, Color.PURPLE);

        private final Color color;
        private final PotionEffect effect;

        Effect(PotionEffect effect, Color color) {
            this.effect = effect;
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public PotionEffect getEffect() {
            return effect;
        }
    }
}