package dev.zenqrt.game;

import dev.zenqrt.utils.Utils;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    private final Map<Player, GamePlayer> gamePlayers;
    private final List<Game> games;

    public GameManager() {
        this.gamePlayers = new HashMap<>();
        this.games = new ArrayList<>();
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        Utils.putOrReplace(gamePlayers, gamePlayer.getPlayer(), gamePlayer);
    }

    public void removeGamePlayer(Player player) {
        gamePlayers.remove(player);
    }

}
