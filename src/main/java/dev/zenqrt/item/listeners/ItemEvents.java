package dev.zenqrt.item.listeners;

import com.github.christian162.annotations.MappedTo;
import com.github.christian162.annotations.Node;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.item.ItemStack;

@Node(name = "item-events", event = ItemEvent.class)
public class ItemEvents {

    @MappedTo
    private final ItemStack item;

    public ItemEvents(ItemStack item) {
        this.item = item;
    }

}
