package dev.zenqrt.item.projectile;

import dev.zenqrt.item.CustomItem;
import dev.zenqrt.utils.Utils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ThrowableItem extends CustomItem {

    private final List<Player> cooldown = new ArrayList<>();

    public ThrowableItem(String id, ItemStack displayItem, double power, double spread) {
        super(id, displayItem);

        createPlayerListener(event -> event.getPlayer().getItemInMainHand(), PlayerUseItemEvent.class, event -> {
            var player = event.getPlayer();

            if(!cooldown.contains(player)) {
                var position = player.getPosition();
                var projectile = createProjectile(player);
                projectile.setInstance(Objects.requireNonNull(player.getInstance()), position.add(0, player.getEyeHeight() - 0.10000000149011612D, 0));
                projectile.shoot(position.add(position.direction().mul(10)), power, spread);
                onThrow(projectile);
                System.out.println("event");
                player.setItemInHand(event.getHand(), event.getItemStack().consume(1));
//                cooldown.add(player);
//                MinecraftServer.getSchedulerManager().buildTask(() -> cooldown.remove(player)).delay(1, TimeUnit.SERVER_TICK).schedule();
                event.setCancelled(true);
            }
        });
    }

    public void onThrow(EntityProjectile projectile) {}

    public abstract EntityProjectile createProjectile(Player shooter);
}
