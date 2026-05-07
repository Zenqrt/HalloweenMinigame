package dev.zenqrt.clownchase.entity.candy;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.utils.text.Messages;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemCandy extends Candy {

    private static final String CONSUME_CANDY_ITEM = "game.consume_candy.item";
    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdlY2Q1ZTg0ODAyMTRiZDYzMzc1NmU2MTkyZTE0NzNjZTI2YWFiYTdhNmZiODJmNTkxODgwYWI0ODc3NTYzIgogICAgfQogIH0KfQ==";

    private final CustomItem item;

    public ItemCandy(Level level, CustomItem item) {
        super(level, TEXTURES);

        this.item = item;
    }

    @Override
    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {
        ItemStack itemStack = item.createItemStack();
        player.getInventory().addItem(itemStack);

        player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_ITEM, itemStack.effectiveName())));
    }
}
