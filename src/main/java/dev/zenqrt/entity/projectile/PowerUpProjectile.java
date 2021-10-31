package dev.zenqrt.entity.projectile;

import dev.zenqrt.potion.PotionEffectHandler;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.entity.EntityUtils;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class PowerUpProjectile extends CollidingProjectile {

    private final String displayName;
    private final PlainTextComponentSerializer plainTextSerializer;

    public PowerUpProjectile(String displayName, @Nullable Player shooter, @NotNull EntityType entityType) {
        super(shooter, entityType);

        this.displayName = displayName;
        this.plainTextSerializer = PlainTextComponentSerializer.plainText();
    }

    @Override
    public void collide(Player player) {
        if(player instanceof FakePlayer) return;

        if(EntityUtils.hasActiveEffect(player, PotionEffect.RESISTANCE)) {
            var pos = player.getPosition();
            instance.playSound(Sound.sound(SoundEvent.BLOCK_LAVA_EXTINGUISH, Sound.Source.PLAYER, 1, 1), pos.x(), pos.y(), pos.z());
            return;
        }

        if(shooter != null) {
            if(shooter == player) return;
            var textColor = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
            var lowercaseName = displayName.toLowerCase(Locale.ENGLISH);
            this.shooter.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1, 1));
            this.shooter.sendMessage(MiniMessage.get().parse(textColor + "You hit <aqua>" + plainTextSerializer.serialize(player.getName()) + textColor + "with your " + lowercaseName + "!"));
            player.sendMessage(MiniMessage.get().parse(textColor + "You got hit with a " + lowercaseName + " by <aqua>" + plainTextSerializer.serialize(shooter.getName()) + textColor + "!"));
        }

        var position = player.getPosition();
        MinestomServer.getPotionEffectManager().applyEffect(player, getWeirdoPotionMan(), getPotionEffectHandler(player));
        player.playSound(getLocalHitSound(), Sound.Emitter.self());
        player.getInstance().playSound(getGlobalHitSound(), position.x(), position.y(), position.z());
        player.sendPacketToViewersAndSelf(getCollisionParticleEmitter().createPacket(player.getPosition().add(0,1,0)));
        this.remove();
    }

    public abstract Potion getWeirdoPotionMan();
    public abstract PotionEffectHandler getPotionEffectHandler(Player player);
    public abstract Sound getLocalHitSound();
    public abstract Sound getGlobalHitSound();
    public abstract ParticleEmitter getCollisionParticleEmitter();

}
