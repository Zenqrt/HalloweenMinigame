package dev.zenqrt.clownchase.game;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.map.ClownChaseMap;
import dev.zenqrt.clownchase.map.MapManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class GameManager {

    private final Map<UUID, ClownChasePlayer> players = new HashMap<>();
    private final Map<Integer, ClownChaseGame> games = new HashMap<>();
    private final AtomicInteger nextGameId;
    private final MapManager mapManager;
    private final ClownChasePlugin plugin;

    public GameManager(ClownChasePlugin plugin, MapManager mapManager) {
        this.plugin = plugin;
        this.mapManager = mapManager;
        this.nextGameId = new AtomicInteger(0);
    }

    public ClownChaseGame createGame(ClownChaseMap map, GameSettings settings) {
        int gameId = nextGameId.incrementAndGet();

        ClownChaseGame game = new ClownChaseGame(gameId, this, this.mapManager, map, this.plugin, settings);
        games.put(gameId, game);

        return game;
    }

    public void deleteGame(int gameId) {
        games.remove(gameId);
    }

    public Optional<ClownChaseGame> findGame(int gameId) {
        return this.games.containsKey(gameId) ? Optional.of(this.games.get(gameId)) : Optional.empty();
    }

    public Optional<ClownChaseGame> findAvailableGame() {
        return games.values().stream()
                .filter(ClownChaseGame::canPlayerJoin)
                .findFirst();
    }

    public Map<Integer, ClownChaseGame> getGames() {
        return Collections.unmodifiableMap(games);
    }

    public ClownChasePlayer addPlayer(UUID uuid) {
        ClownChasePlayer gamePlayer = new ClownChasePlayer(uuid);
        players.put(gamePlayer.getUniqueId(), gamePlayer);

        return gamePlayer;
    }

    public ClownChasePlayer addPlayer(@NotNull Player player) {
        ClownChasePlayer gamePlayer = new ClownChasePlayer(player);
        players.put(player.getUniqueId(), gamePlayer);

        return gamePlayer;
    }

    public void removePlayer(ClownChasePlayer gamePlayer) {
        players.remove(gamePlayer.getUniqueId());

        if (gamePlayer.getGame() != null)
            gamePlayer.getGame().removePlayer(gamePlayer);
    }

    public Optional<ClownChasePlayer> findPlayer(UUID uuid) {
        return this.players.containsKey(uuid) ? Optional.of(this.players.get(uuid)) : Optional.empty();
    }

    public Map<UUID, ClownChasePlayer> getPlayers() {
        return players;
    }
}
