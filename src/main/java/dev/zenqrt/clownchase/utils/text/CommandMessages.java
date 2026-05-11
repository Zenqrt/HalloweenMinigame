package dev.zenqrt.clownchase.utils.text;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class CommandMessages {

    private CommandMessages() {
    }

    public static int sendSuccess(CommandSourceStack source, Component message) {
        source.getSender().sendMessage(message.color(NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    public static int sendSuccess(CommandSourceStack source, String message) {
        return sendSuccess(source, Component.text(message));
    }

    public static void sendInfo(CommandSourceStack source, Component message) {
        source.getSender().sendMessage(message.color(NamedTextColor.GRAY));
    }

    public static void sendInfo(CommandSourceStack source, String message) {
        sendInfo(source, Component.text(message));
    }

    public static int sendResponse(CommandSourceStack source, Component message) {
        source.getSender().sendMessage(message);

        return Command.SINGLE_SUCCESS;
    }
}
