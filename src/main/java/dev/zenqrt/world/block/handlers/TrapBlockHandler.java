package dev.zenqrt.world.block.handlers;

import dev.zenqrt.game.halloween.HalloweenGame;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.utils.Utils;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

        // TODO: if cant find fix, use hashmaps
        MinecraftServer.getSchedulerManager().buildTask(() -> playerPlacement.getInstance().setBlock(playerPlacement.getBlockPosition(), block))
                .delay(1, TimeUnit.SERVER_TICK)
                .schedule();
    }

    @Override
    default void onTouch(@NotNull Touch touch) {
        if(!(touch.getTouching() instanceof Player player)) return;

        var block = touch.getBlock();
        var tag = block.getTag(TRAP_OWNER_TAG);
        var textColor = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
        var amount = 5;

        Utils.runIfExisting(MinestomServer.getGameManager().findGamePlayer(player), gamePlayer -> Utils.runIfExisting(gamePlayer.getCurrentGame(), game -> {
            if(game instanceof HalloweenGame halloweenGame) {
                halloweenGame.removeCandy(gamePlayer, amount);
            }
        }));

        if(tag != null) {
            var trapOwner = MinecraftServer.getConnectionManager().getPlayer(UUID.fromString(tag));
            if(trapOwner != null) {
                var plainText = PlainTextComponentSerializer.plainText();
                var lowercaseName = getDisplayName().toLowerCase(Locale.ENGLISH);
                Utils.runIfExisting(MinestomServer.getGameManager().findGamePlayer(trapOwner), gamePlayer -> Utils.runIfExisting(gamePlayer.getCurrentGame(), game -> {
                    if(game instanceof HalloweenGame halloweenGame) {
                        halloweenGame.addCandy(gamePlayer, 5);
                    }
                }));
                // You caught {player} in your {trap}, stealing x candies!
                trapOwner.sendMessage(MiniMessage.get().parse(textColor + "You caught <aqua>" + plainText.serialize(player.getName()) + textColor + " in your " + lowercaseName + ", stealing " + HalloweenGame.VALUE_COLOR + amount + textColor + " candies!"));
                player.sendMessage(MiniMessage.get().parse("<aqua>" + plainText.serialize(trapOwner.getName()) + textColor + " stole " + HalloweenGame.VALUE_COLOR + amount + textColor + " candy from you using their " + lowercaseName + "!"));
            }
        }
        touch.getInstance().setBlock(touch.getBlockPosition(), Block.AIR);
        onTrap(touch);
        player.playSound(getCatchSound());
    }
}
