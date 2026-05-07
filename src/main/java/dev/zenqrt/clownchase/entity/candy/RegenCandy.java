package dev.zenqrt.clownchase.entity.candy;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.utils.text.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.level.Level;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class RegenCandy extends SpecialCandy {

    private static final String CANDY_TITLE = "game.candy.regen.title";
    private static final String CONSUME_CANDY_REGEN = "game.consume_candy.regen";
    private static final Sound CONSUME_SOUND = Sound.sound(Key.key("minecraft:block.amethyst_block.resonate"), Sound.Source.MASTER, 10F, 1);

    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE3NzUyYmRjY2FkNmE0Njc4YjZlYWE1OTIzNzM4YWE2M2QwYjc1MTcxODAzMjkyMTZmMGFhMzRmZWI1NWViIgogICAgfQogIH0KfQ==";
    private static final int REGEN_AMOUNT = 2;

    public RegenCandy(Level level) {
        super(level, Component.translatable(CANDY_TITLE, TextColor.color(0xFFA3DE)).decorate(TextDecoration.BOLD), TEXTURES);
    }

    @Override
    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {
        if (player.getHealth() < game.getGameSettings().maxHealth())
            player.setHealth(Math.min(player.getHealth() + REGEN_AMOUNT, game.getGameSettings().maxHealth()));

        Particle.HEART.builder()
                .allPlayers()
                .source(player)
                .count(5)
                .offset(1, 2, 1)
                .location(player.getLocation())
                .spawn();

        player.playSound(CONSUME_SOUND, Sound.Emitter.self());
        player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_REGEN, NamedTextColor.YELLOW,
                Component.text(REGEN_AMOUNT / 2 + "❤", NamedTextColor.RED))));
    }
}
