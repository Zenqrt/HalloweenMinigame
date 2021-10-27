package dev.zenqrt.entity.projectile;

import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
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
        if(shooter != null) {
            if(shooter == player) return;
            var textColor = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
            var lowercaseName = displayName.toLowerCase(Locale.ENGLISH);
            this.shooter.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1, 1));
            this.shooter.sendMessage(MiniMessage.get().parse(textColor + "You hit <aqua>" + plainTextSerializer.serialize(player.getName()) + textColor + "with your " + lowercaseName + "!"));
            player.sendMessage(MiniMessage.get().parse(textColor + "You got hit with a " + lowercaseName + " by <aqua>" + plainTextSerializer.serialize(shooter.getName()) + textColor + "!"));
        }

        var position = player.getPosition();
        player.addEffect(getWeirdoPotionMan());
        player.playSound(getLocalHitSound(), Sound.Emitter.self());
        player.getInstance().playSound(getGlobalHitSound(), position.x(), position.y(), position.z());
        player.sendPacketToViewersAndSelf(getCollisionParticleEmitter().createPacket(player.getPosition().add(0,1,0)));
        this.remove();
    }

    public abstract Potion getWeirdoPotionMan();
    public abstract Sound getLocalHitSound();
    public abstract Sound getGlobalHitSound();
    public abstract ParticleEmitter getCollisionParticleEmitter();

}
