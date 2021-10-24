package dev.zenqrt.item;

import dev.zenqrt.item.listeners.ItemEvents;
import dev.zenqrt.server.MinestomServer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomItem {

    private static final String ITEM_ID_TAG = "item-id";

    private final List<EventListener<? extends Event>> listeners;
    private final ItemStack displayItem;
    private final String id;

    public CustomItem(String id, ItemStack displayItem) {
        this.id = id;
        this.listeners = new ArrayList<>();
        this.displayItem = displayItem;
    }

    public ItemStack create() {
        return displayItem.withTag(Tag.String(ITEM_ID_TAG), id);
    }

    public <T extends ItemEvent> EventListener<T> createItemListener(EventListener.Builder<T> builder) {
        var listener = builder.filter(event -> verifyItemTag(event.getItemStack())).build();
        registerListener(listener);
        return listener;
    }

    public <T extends ItemEvent> EventListener<T> createItemListener(Class<T> eventType, Consumer<T> function) {
        return createItemListener(EventListener.builder(eventType).handler(function));
    }

    public <T extends PlayerEvent> EventListener<T> createPlayerListener(Function<Player, ItemStack> itemStackFunction, EventListener.Builder<T> builder) {
        var listener = builder.filter(event -> verifyItemTag(itemStackFunction.apply(event.getPlayer()))).build();
        registerListener(listener);
        return listener;
    }

    public <T extends PlayerEvent> EventListener<T> createPlayerListener(Function<Player, ItemStack> itemStackFunction, Class<T> eventType, Consumer<T> function) {
        return createPlayerListener(itemStackFunction, EventListener.builder(eventType).handler(function));
    }

    private void registerListener(EventListener<?> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(listener);
        listeners.add(listener);
    }

    private boolean verifyItemTag(ItemStack itemStack) {
        var tag = itemStack.getTag(Tag.String(ITEM_ID_TAG));
        return tag != null && tag.equals(id);
    }

    public void removeListener(EventListener<? extends ItemEvent> listener) {
        MinecraftServer.getGlobalEventHandler().removeListener(listener);
        listeners.remove(listener);
    }

}
