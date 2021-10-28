package dev.zenqrt.game.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.GamePlayer;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;

@Node(name = "game-events", event = PlayerEvent.class)
public class GameEvents {

    private final GameManager gameManager;

    public GameEvents(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Listen
    public void onPlayerLogin(PlayerLoginEvent event) {
        gameManager.addGamePlayer(new GamePlayer(event.getPlayer()));
    }

    @Listen
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        gameManager.removeGamePlayer(event.getPlayer());
    }
}
