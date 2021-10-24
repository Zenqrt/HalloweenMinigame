package dev.zenqrt.game;

import dev.zenqrt.utils.Utils;
import net.minestom.server.entity.Player;

import java.util.*;

public class GameManager {

    private final Map<Player, GamePlayer> gamePlayers;
    private final Set<Game> games;

    public GameManager() {
        this.gamePlayers = new HashMap<>();
        this.games = new HashSet<>();
    }

    public void registerGame(Game game) {
        games.add(game);
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        Utils.putOrReplace(gamePlayers, gamePlayer.getPlayer(), gamePlayer);
    }

    public void removeGamePlayer(Player player) {
        gamePlayers.remove(player);
    }

    public GamePlayer findGamePlayer(Player player) {
        return gamePlayers.get(player);
    }

}
