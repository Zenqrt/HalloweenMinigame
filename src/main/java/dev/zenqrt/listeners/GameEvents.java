package dev.zenqrt.listeners;

import com.github.christian162.annotations.Filter;
import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.trait.PlayerEvent;

@Node(name = "game-events", event = PlayerEvent.class)
public class GameEvents {

    @Filter
    public boolean filter(PlayerEvent event) {
        return false;
    }

    @Listen
    public void onBlockBreak(PlayerBlockBreakEvent event) {
        event.setCancelled(true);
    }

}
