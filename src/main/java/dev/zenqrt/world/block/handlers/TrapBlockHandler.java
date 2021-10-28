package dev.zenqrt.world.block.handlers;

import dev.zenqrt.utils.chat.ChatUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

public interface TrapBlockHandler extends BlockHandler {

    Tag<String> TRAP_OWNER_TAG = Tag.String("TrapOwner");

    void onTrap(Touch touch);
    String getDisplayName();
    Sound getCatchSound();

    @Override
    default void onPlace(@NotNull Placement placement) {
        if(!(placement instanceof PlayerPlacement playerPlacement)) return;
        var block = playerPlacement.getBlock()
                .withTag(TRAP_OWNER_TAG, playerPlacement.getPlayer().getUuid().toString());
        MinecraftServer.getSchedulerManager().buildTask(() -> playerPlacement.getInstance().setBlock(playerPlacement.getBlockPosition(), block))
                .delay(1, TimeUnit.SERVER_TICK)
                .schedule();
    }

    @Override
    default void onTouch(@NotNull Touch touch) {
        if(!(touch.getTouching() instanceof Player player)) return;

        var block = touch.getBlock();
        var tag = block.getTag(TRAP_OWNER_TAG);
        var textColor = TextColor.fromHexString(ChatUtils.TEXT_COLOR_HEX);
        if(tag != null) {
            var trapOwner = MinecraftServer.getConnectionManager().getPlayer(UUID.fromString(tag));
            if(trapOwner != null) {
                var lowercaseName = getDisplayName().toLowerCase(Locale.ENGLISH);
                trapOwner.sendMessage(Component.text("You caught ", textColor)
                        .append(player.getName().color(NamedTextColor.AQUA))
                        .append(Component.text(" in your " + lowercaseName + "!"))
                );
                player.sendMessage(Component.text("You got caught in ", textColor)
                        .append(player.getName().color(NamedTextColor.AQUA))
                        .append(Component.text("'s " + lowercaseName + "!"))
                );
            }
        }
        touch.getInstance().setBlock(touch.getBlockPosition(), Block.AIR);
        onTrap(touch);
        player.playSound(getCatchSound());
    }
}
