package dev.zenqrt.clownchase.event.listeners;

import dev.zenqrt.clownchase.map.MapManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public final class GameWorldListeners implements Listener {

    private final MapManager mapManager;

    public GameWorldListeners(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (this.mapManager.isGameWorld(event.getBlock().getWorld()))
            event.setCancelled(true);
    }

}
