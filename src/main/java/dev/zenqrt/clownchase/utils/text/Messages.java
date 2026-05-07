package dev.zenqrt.clownchase.utils.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public final class Messages {

    private Messages() {
    }

    public static Component consumeSpecialCandy(Component message) {
        return Component.empty()
                .append(Component.translatable("game.consume_candy.special_prefix", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.space())
                .append(message.color(NamedTextColor.YELLOW));
    }

    public static Component candyText(int candies) {
        return Component.text(candies, NamedTextColor.WHITE)
                .append(Component.text(" ♧", NamedTextColor.GREEN));
    }

    public static Component seconds(int seconds) {
        return Component.text(seconds + "s", TextColorPresets.NUMBER);
    }

    public static Component username(Player player) {
        return Component.text(player.getName(), TextColorPresets.USERNAME);
    }

}
