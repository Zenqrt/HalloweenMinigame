package dev.zenqrt.item.powerup;

import dev.zenqrt.item.CustomItem;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

public abstract class TrapItem extends CustomItem {

    public TrapItem(String id, ItemStack displayItem) {
        super(id, displayItem);

        createPlayerListener(event -> event.getPlayer().getItemInMainHand(), PlayerBlockInteractEvent.class, event -> {

        });
    }

    public abstract Block getBlock();
}
