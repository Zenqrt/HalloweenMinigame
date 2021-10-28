package dev.zenqrt.item.powerup;

import dev.zenqrt.world.block.handlers.SlownessTrapHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class SlownessTrapItem extends TrapItem {

    private static final SlownessTrapHandler HANDLER = new SlownessTrapHandler();

    public SlownessTrapItem() {
        super("slowness_trap",
                ItemStack.builder(Material.STONE_PRESSURE_PLATE)
                        .displayName(Component.text("Slowness Trap", NamedTextColor.DARK_PURPLE)
                                .decoration(TextDecoration.ITALIC, false)
                ).build()
        );
    }

    @Override
    public Block getBlock() {
        return Block.STONE_PRESSURE_PLATE.withHandler(HANDLER);
    }
}
