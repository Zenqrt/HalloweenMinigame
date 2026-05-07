package dev.zenqrt.clownchase.entity.candy;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.utils.text.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemCandy extends SpecialCandy {

    private static final String CONSUME_CANDY_ITEM = "game.consume_candy.item";
    private static final String CANDY_TITLE = "game.candy.item.title";
    private static final Sound CONSUME_SOUND = Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 1, 2);
    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdlY2Q1ZTg0ODAyMTRiZDYzMzc1NmU2MTkyZTE0NzNjZTI2YWFiYTdhNmZiODJmNTkxODgwYWI0ODc3NTYzIgogICAgfQogIH0KfQ==";

    private final CustomItem item;

    public ItemCandy(Level level, CustomItem item) {
        super(level, Component.translatable(CANDY_TITLE, TextColor.color(0x68F78B)).decorate(TextDecoration.BOLD), TEXTURES);

        this.item = item;
    }

    @Override
    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {
        ItemStack itemStack = item.createItemStack();
        player.getInventory().addItem(itemStack);

        player.playSound(CONSUME_SOUND, Sound.Emitter.self());
        player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_ITEM, itemStack.effectiveName())));
    }
}
