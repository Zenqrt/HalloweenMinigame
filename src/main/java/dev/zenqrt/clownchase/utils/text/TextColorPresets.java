package dev.zenqrt.clownchase.utils.text;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class TextColorPresets {

    public static final TextColor TEXT = TextColor.color(0xfc86c1);
    public static final TextColor NUMBER = TextColor.color(0x40ed6e);
    public static final TextColor USERNAME = NamedTextColor.YELLOW;
    public static final TextColor ERROR = NamedTextColor.RED;

    public static final TextColor SCOREBOARD_TEXT = NamedTextColor.LIGHT_PURPLE;

    private TextColorPresets() {
    }

}
