package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.game.base.GameState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class TeleportPlayersToLobbyGameState extends GameState {

    private final List<ClownChasePlayer> playersTeleported = new ArrayList<>();
    private final Location lobbySpawn;
    private final GameManager gameManager;
    private final ClownChaseGame game;

    public TeleportPlayersToLobbyGameState(ClownChaseGame game, GameManager gameManager, Location lobbySpawn) {
        this.game = game;
        this.gameManager = gameManager;
        this.lobbySpawn = lobbySpawn;
    }

    @Override
    protected void onStateStart() {
        final int playerSize = this.game.getPlayers().size();

        this.game.getPlayers().forEach((_, gamePlayer) -> {
            Player player = gamePlayer.validatePlayer();

            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.clearActivePotionEffects();

            player.teleportAsync(lobbySpawn)
                    .thenRunAsync(() -> playersTeleported.add(gamePlayer));
        });

        new TeleportCheckTask(playerSize, 200)
                .runTaskTimer(this.game.getPlugin(), 0, 20);
    }

    @Override
    protected void onStateEnd() {
        playersTeleported.forEach(gamePlayer -> this.gameManager.leaveGame(gamePlayer, this.game));
    }

    private class TeleportCheckTask extends BukkitRunnable {

        private int currentTime;
        private final int playerSize;

        TeleportCheckTask(int playerSize, int timeout) {
            this.playerSize = playerSize;
            this.currentTime = timeout;
        }

        @Override
        public void run() {
            int count = TeleportPlayersToLobbyGameState.this.playersTeleported.size();

            if (count < playerSize && --currentTime > 0)
                return;

            TeleportPlayersToLobbyGameState.this.game.nextState();
            this.cancel();
        }
    }

}
