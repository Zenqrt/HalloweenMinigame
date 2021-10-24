package dev.zenqrt.item.powerup;

import dev.zenqrt.entity.projectile.StunBall;
import dev.zenqrt.item.projectile.ThrowableItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class StunBallItem extends ThrowableItem {

    public StunBallItem() {
        super("stun_ball", ItemStack.builder(Material.SNOWBALL)
                .displayName(Component.text("Stun Ball", NamedTextColor.LIGHT_PURPLE)).build(), 1.25, 0);
    }

    @Override
    public EntityProjectile createProjectile(Player shooter) {
        return new StunBall(shooter);
    }
}
