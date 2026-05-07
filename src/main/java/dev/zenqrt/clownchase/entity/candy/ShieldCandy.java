package dev.zenqrt.clownchase.entity.candy;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.utils.text.Messages;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Player;

public final class ShieldCandy extends Candy {

    private static final String CONSUME_CANDY_SHIELD_ALREADY = "game.consume_candy.shield.already";
    private static final String CONSUME_CANDY_SHIELD = "game.consume_candy.shield";
    private static final Sound SHIELD_EQUIP_SOUND = Sound.sound(Key.key("minecraft:item.trident.return"), Sound.Source.MASTER, 1, 1.1F);

    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QxMDgxODMwZTY0NGQyOWVlMTk0YmQ2NWU3NDA5OWZiOTc0N2RhOGMzZDg4MTdmOTQ4MjllNjVjZmNlNDlhIgogICAgfQogIH0KfQ==";

    public ShieldCandy(Level level) {
        super(level, TEXTURES);
    }

    @Override
    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {
        if (playerData.isShielded()) {
            player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_SHIELD_ALREADY)));
        } else {
            playerData.setShielded(true);

            player.playSound(SHIELD_EQUIP_SOUND, Sound.Emitter.self());
            player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_SHIELD, Component.text("1 hit", TextColorPresets.NUMBER))));
        }
    }
}
