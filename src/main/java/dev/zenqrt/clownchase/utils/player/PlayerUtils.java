package dev.zenqrt.clownchase.utils.player;

import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRemoveEvent;

public final class PlayerUtils {

    private PlayerUtils() {}

    public static void forceRemove(Player player, EntityRemoveEvent.Cause cause) {
        ((CraftPlayer) player).getHandle().remove(Entity.RemovalReason.DISCARDED, cause);
    }

}
