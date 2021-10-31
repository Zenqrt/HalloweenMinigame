package dev.zenqrt.entity.candy;

import dev.zenqrt.potion.*;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.timer.countdown.CountdownRunnable;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.time.TimeUnit;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Objects;

public class EffectCandy extends Candy {

    private static final ParticleEmitter CONSUME_PARTICLE_EMITTER = new ParticleEmitter(Particle.ENTITY_EFFECT, Vec.ZERO, 1, 100, true);

    private final Effect effect;

    public EffectCandy(Effect effect) {
        super(effect.color);

        this.effect = effect;
    }

    @Override
    public void consume(Player player) {
        super.consume(player);

        var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);

        MinestomServer.getPotionEffectManager().applyEffect(player, new Potion(effect.effect, (byte) 2, 200), Objects.requireNonNull(effect.createHandler(player)));
        player.sendMessage(MiniMessage.get().parse(color + "You have received the <yellow>" + effect.name().toLowerCase() + color + " effect for <yellow>10 " + color + "seconds!"));

        new CountdownRunnable(10) {
            @Override
            public void beforeIncrement() {
                var pitch = (20f - timer) / 10f;
                player.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 10, pitch), Sound.Emitter.self());
                player.sendPacketToViewersAndSelf(CONSUME_PARTICLE_EMITTER.createPacket(player.getPosition()));
            }
        }.repeat(Duration.of(75, TimeUnit.MILLISECOND)).schedule();
    }

    public enum Effect {
        SPEED(PotionEffect.SPEED, SpeedEffectHandler.class, Color.ORANGE),
        DOUBLE_CANDY(PotionEffect.LUCK, EmptyEffectHandler.class, Color.GREEN),
        RESISTANCE(PotionEffect.RESISTANCE, EmptyEffectHandler.class, Color.BLUE),
        REGENERATION(PotionEffect.REGENERATION, RegenerationEffectHandler.class, Color.PINK);
//        INVISIBILITY(PotionEffect.INVISIBILITY, InvisibilityEffectHandler.class, Color.YELLOW);

        private final Color color;
        private final Class<? extends PotionEffectHandler> handler;
        private final PotionEffect effect;

        Effect(PotionEffect effect, Class<? extends PotionEffectHandler> handler, Color color) {
            this.effect = effect;
            this.handler = handler;
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public PotionEffectHandler createHandler(Player player) {
            try {
                return handler.getDeclaredConstructor(Player.class).newInstance(player);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }

        public PotionEffect getEffect() {
            return effect;
        }
    }
}
