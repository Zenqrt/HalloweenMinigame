package dev.zenqrt.game;

import net.minestom.server.entity.Player;

public class GamePlayer {

    private final Player player;
    private Game currentGame;

    private GamePlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public static GamePlayer createGamePlayer(Player player) {
        var gamePlayer = new GamePlayer(player);
        return gamePlayer;
    }

}
