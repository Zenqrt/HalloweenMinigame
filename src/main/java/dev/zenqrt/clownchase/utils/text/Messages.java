package dev.zenqrt.clownchase.utils.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class Messages {

    private Messages() {
    }

    public static Component consumeSpecialCandy(Component message) {
        return Component.empty()
                .append(Component.translatable("game.consume_candy.special_prefix", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.space())
                .append(message.color(NamedTextColor.YELLOW));
    }

}
