package dev.zenqrt.game.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.GameState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.network.player.PlayerConnection;

@Node(name = "game-events", event = PlayerEvent.class)
public class GameEvents {

    private final GameManager gameManager;

    public GameEvents(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Listen
    public void onPlayerLogin(PlayerLoginEvent event) {
        var player = event.getPlayer();
        if(!(player instanceof FakePlayer)) {
            var mainGame = gameManager.getMainGame();
            if(mainGame != null && mainGame.getState() != GameState.WAITING) {
                event.getPlayer().kick("Game is active");
            }

            var gamePlayer = new GamePlayer(player);
            gameManager.addGamePlayer(gamePlayer);
            player.setBoundingBox(2, 2, 2);
        }
    }

    @Listen
    public void onPlayerSpawn(PlayerSpawnEvent event) {
        if(!event.isFirstSpawn()) return;

        var player = event.getPlayer();
        if(!(player instanceof FakePlayer)) {
            System.out.println("SKJDFBF");
            System.out.println("SKJDFBF");
            System.out.println("SKJDFBF");
            System.out.println("SKJDFBF");
            System.out.println("SKJDFBF");
            System.out.println("SKJDFBF");
            var gamePlayer = gameManager.findGamePlayer(player);
            var mainGame = gameManager.getMainGame();
            if(mainGame != null) {
                mainGame.join(gamePlayer);
            }
        }
    }

    @Listen
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        gameManager.removeGamePlayer(event.getPlayer());
    }
}
