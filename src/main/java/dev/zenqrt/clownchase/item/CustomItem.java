package dev.zenqrt.clownchase.item;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class CustomItem {

    private static final NamespacedKey ITEM_KEY = new NamespacedKey("clownchase", "custom_item");

    protected final ItemStack baseItem;
    private final String itemId;

    public CustomItem(String itemId, Material material, String name, ItemRarity rarity) {
        this.itemId = itemId;
        this.baseItem = new ItemStack(material);
        this.baseItem.setData(DataComponentTypes.ITEM_NAME, Component.text(name));
        this.baseItem.setData(DataComponentTypes.RARITY, rarity);

        if (!this.baseItem.editPersistentDataContainer(
                dataContainer -> dataContainer.set(ITEM_KEY, PersistentDataType.STRING, itemId)))
            throw new RuntimeException("Could not edit persistent data container for '" + itemId + "'");
    }

    public ItemStack createItemStack() {
        return baseItem.clone();
    }

    public String getItemId() {
        return itemId;
    }

    protected static boolean isItemUsedInGame(CustomItem customItem, ItemStack itemStack, ClownChaseGame game, UUID playerUuid) {
        return isItem(customItem, itemStack) && game.hasPlayer(playerUuid);
    }

    private static boolean isItem(CustomItem customItem, ItemStack itemStack) {
        PersistentDataContainerView dataContainer = itemStack.getPersistentDataContainer();

        return dataContainer.has(ITEM_KEY) && Objects.equals(dataContainer.get(ITEM_KEY, PersistentDataType.STRING), customItem.getItemId());
    }

    public static class Listeners implements Listener {

        protected final ClownChaseGame game;

        public Listeners(ClownChaseGame game) {
            this.game = game;
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

    }

}
