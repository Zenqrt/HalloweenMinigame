package dev.zenqrt.item.projectile;

import dev.zenqrt.item.CustomItem;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.item.ItemStack;

import java.util.Objects;

public abstract class ThrowableItem extends CustomItem {

    public ThrowableItem(String id, ItemStack displayItem, double power, double spread) {
        super(id, displayItem);

        createPlayerListener(Player::getItemInMainHand, PlayerHandAnimationEvent.class, event -> {
            var player = event.getPlayer();
            var position = player.getPosition();
            var projectile = createProjectile(player);
            projectile.setInstance(Objects.requireNonNull(player.getInstance()), position.add(0, player.getEyeHeight() - 0.10000000149011612D, 0));
            projectile.shoot(position.add(position.direction().mul(10)), power, spread);
            onThrow(projectile);
        });
    }

    public void onThrow(EntityProjectile projectile) {}

    public abstract EntityProjectile createProjectile(Player shooter);
}
