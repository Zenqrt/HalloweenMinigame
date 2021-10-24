package dev.zenqrt.game.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.GamePlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.InstanceContainer;

@Node(name = "game-events", event = PlayerEvent.class)
public class GameEvents {

    private final GameManager gameManager;
    private final InstanceContainer instanceContainer;

    public GameEvents(GameManager gameManager, InstanceContainer instanceContainer) {
        this.gameManager = gameManager;
        this.instanceContainer = instanceContainer;
    }

    @Listen
    public void onPlayerLogin(PlayerLoginEvent event) {
        final var player = event.getPlayer();

        event.setSpawningInstance(instanceContainer);
        player.setRespawnPoint(new Pos(0, 42, 0));
        player.setGameMode(GameMode.ADVENTURE);

        if(gameManager.findGamePlayer(player) == null) {
            gameManager.addGamePlayer(new GamePlayer(player));
        }
    }

}
