package dev.zenqrt.game;

import net.minestom.server.entity.Player;

public class GamePlayer {

    private final Player player;

    private GamePlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public static GamePlayer createGamePlayer(Player player) {
        var gamePlayer = new GamePlayer(player);
        return gamePlayer;
    }

}
