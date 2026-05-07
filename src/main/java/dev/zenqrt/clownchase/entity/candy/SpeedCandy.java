package dev.zenqrt.clownchase.entity.candy;

import com.destroystokyo.paper.ParticleBuilder;
import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.utils.text.Messages;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.level.Level;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public final class SpeedCandy extends Candy {

    private static final String CONSUME_CANDY_SPEED = "game.consume_candy.speed";

    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI1NDI1NzU0MjMyNDZmYWE3M2EzNzYzZTFiNDZkY2ZhM2Y0NmVkYjliMTVhY2IwYmMxMTkwMTQ2ZWUxOWQiCiAgICB9CiAgfQp9";
    private static final int DURATION = 100;  // 5 seconds

    public SpeedCandy(Level level) {
        super(level, TEXTURES);
    }

    @Override
    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION, 1));

        playConsumeEffect(player, game.getPlugin());
        player.sendMessage(
                Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_SPEED, NamedTextColor.YELLOW,
                        Component.text("Speed I", NamedTextColor.AQUA), Component.text(DURATION / 20 + "s", TextColorPresets.NUMBER)))
        );
    }

    private static void playConsumeEffect(Player player, ClownChasePlugin plugin) {
        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (iterations >= 10) {
                    this.cancel();
                    return;
                }

                float pitch = iterations / 10F + 1;

                player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1, pitch), Sound.Emitter.self());
                createConsumeParticle()
                        .location(player.getLocation())
                        .spawn();

                ++iterations;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private static ParticleBuilder createConsumeParticle() {
        return Particle.ENTITY_EFFECT.builder()
                .extra(1)
                .count(50)
                .offset(0.25, 0.5, 0.25)
                .color(Color.AQUA);
    }
}
