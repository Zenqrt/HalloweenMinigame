package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.lobby.LobbyManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class TeleportPlayersToLobbyGameState extends GameState {

    private final List<ClownChasePlayer> playersTeleported = new ArrayList<>();
    private final LobbyManager lobbyManager;
    private final GameManager gameManager;
    private final ClownChaseGame game;

    public TeleportPlayersToLobbyGameState(ClownChaseGame game, GameManager gameManager, LobbyManager lobbyManager) {
        this.game = game;
        this.gameManager = gameManager;
        this.lobbyManager = lobbyManager;
    }

    @Override
    protected void onStateStart() {
        final int playerSize = this.game.getPlayers().size();

        this.game.getPlayers().forEach(
                (_, gamePlayer) ->
                        this.lobbyManager.sendToLobby(gamePlayer.validatePlayer())
                                .thenRunAsync(() -> playersTeleported.add(gamePlayer)));

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
