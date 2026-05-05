package dev.zenqrt.clownchase.event.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class GameplayListeners implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (!(event.getDamager() instanceof Zombie)) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(2);
    }
}
