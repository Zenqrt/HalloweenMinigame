package dev.zenqrt.utils.chat;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @see <a href="https://docs.adventure.kyori.net/minimessage.html#usage">https://docs.adventure.kyori.net/minimessage.html#usage</a>
 */
public enum ParsedColor implements ParsedChat {
    BLACK("black"),
    DARK_BLUE("dark_blue"),
    DARK_GREEN("dark_green"),
    DARK_AQUA("dark_aqua"),
    DARK_RED("dark_red"),
    DARK_PURPLE("dark_purple"),
    GOLD("gold"),
    GRAY("gray"),
    DARK_GRAY("dark_gray"),
    BLUE("blue"),
    GREEN("green"),
    AQUA("aqua"),
    RED("red"),
    LIGHT_PURPLE("light_purple"),
    YELLOW("yellow"),
    WHITE("white"),
    OBFUSCATED("obfuscated"),
    BOLD("bold"),
    STRIKETHROUGH("strikethrough"),
    UNDERLINE("underline"),
    ITALIC("italic"),
    RESET("reset"),
    RAINBOW("rainbow"),
    PRE("pre")
    ;

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
