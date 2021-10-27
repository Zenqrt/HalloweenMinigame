package dev.zenqrt.utils.chat;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @see <a href="https://docs.adventure.kyori.net/minimessage.html#usage">https://docs.adventure.kyori.net/minimessage.html#usage</a>
 */
@SuppressWarnings("unused")
public enum ParsedColor implements ParsedChat {
    AQUA("aqua"),
    BLACK("black"),
    BLUE("blue"),
    BOLD("bold"),
    DARK_AQUA("dark_aqua"),
    DARK_BLUE("dark_blue"),
    DARK_GRAY("dark_gray"),
    DARK_GREEN("dark_green"),
    DARK_PURPLE("dark_purple"),
    DARK_RED("dark_red"),
    GOLD("gold"),
    GRAY("gray"),
    GREEN("green"),
    ITALIC("italic"),
    LIGHT_PURPLE("light_purple"),
    OBFUSCATED("obfuscated"),
    PRE("pre"),
    RAINBOW("rainbow"),
    RED("red"),
    RESET("reset"),
    STRIKETHROUGH("strikethrough"),
    UNDERLINE("underline"),
    WHITE("white"),
    YELLOW("yellow");

    private final String name;

    ParsedColor(String parsed) {
        this.name = parsed;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return openingTag(name);
    }

    public static String of(String hexString) {
        return openingTag(hexString);
    }

    public static String of(int rgb) {
        return openingTag(Integer.toHexString(rgb));
    }

    public static String gradient(ParsedColor... colors) {
        return createGradientText(colors, color -> ":" + color.name);
    }

    public static String gradient(String... hexStrings) {
        return createGradientText(hexStrings, string -> ":" + string);
    }

    public static String gradient(Integer... rgb) {
        return createGradientText(rgb, Integer::toHexString);
    }

    public static String ignoreParse(String string) {
        return PRE + string + PRE.closingTag();
    }

    private static <T> String createGradientText(T[] list, Function<T, String> function) {
        return openingTag("gradient" + Stream.of(list)
                .map(function)
                .collect(Collectors.joining()));
    }

    private static String openingTag(String string) {
        return "<" + string + ">";
    }

    public String closingTag() {
        return openingTag("/" + name);
    }

}
