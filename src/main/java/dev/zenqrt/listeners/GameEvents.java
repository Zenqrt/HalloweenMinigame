package dev.zenqrt.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.trait.PlayerEvent;

@Node(name = "game-events", event = PlayerEvent.class)
public class GameEvents {

    @Listen
    public void onBlockBreak(PlayerBlockBreakEvent event) {
        event.setCancelled(true);
    }

}
