package dev.zenqrt.item.powerup;

import dev.zenqrt.entity.projectile.InkEgg;
import dev.zenqrt.item.projectile.ThrowableItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class InkEggItem extends ThrowableItem {

    public InkEggItem() {
        super("ink_egg", ItemStack.builder(Material.EGG)
                        .displayName(Component.text("Ink Egg", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).build(), 1.25, 0);
    }

    @Override
    public EntityProjectile createProjectile(Player shooter) {
        return new InkEgg(shooter);
    }
}
