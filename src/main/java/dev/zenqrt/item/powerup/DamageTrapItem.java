package dev.zenqrt.item.powerup;

import dev.zenqrt.world.block.handlers.DamageTrapHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class DamageTrapItem extends TrapItem {

    private static final DamageTrapHandler HANDLER = new DamageTrapHandler();

    public DamageTrapItem() {
        super("damage_trap", ItemStack.builder(Material.CRIMSON_PRESSURE_PLATE)
                .displayName(Component.text("Stun Ball", NamedTextColor.LIGHT_PURPLE)
                        .decoration(TextDecoration.ITALIC, false))
                .build());
    }

    @Override
    public Block getBlock() {
        return Block.CRIMSON_PRESSURE_PLATE.withHandler(HANDLER);
    }
}
