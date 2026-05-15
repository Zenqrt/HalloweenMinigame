package dev.zenqrt.clownchase.lobby;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public final class LobbyManager {

    private final Location lobbySpawn;

    public LobbyManager(Location lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public CompletableFuture<Boolean> sendToLobby(Player player) {
        setupLobbyPlayer(player);

        return player.teleportAsync(lobbySpawn);
    }

    public void setupLobbyPlayer(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.clearActivePotionEffects();
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }
}
