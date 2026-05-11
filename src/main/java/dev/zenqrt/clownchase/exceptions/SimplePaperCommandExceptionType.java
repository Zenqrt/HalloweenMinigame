package dev.zenqrt.clownchase.exceptions;

import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;

public final class SimplePaperCommandExceptionType implements CommandExceptionType {

    private final Component message;

    public SimplePaperCommandExceptionType(Component message) {
        this.message = message;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, PaperAdventure.asVanilla(message));
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
