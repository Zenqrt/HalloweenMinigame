package dev.zenqrt.clownchase.event.listeners;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PlayerActivityListeners implements Listener {

    private final GameManager gameManager;
    private final ClownChasePlugin plugin;

    public PlayerActivityListeners(ClownChasePlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        this.gameManager.addPlayer(event.getUniqueId());
    }

    @EventHandler
    public void onSpawn(AsyncPlayerSpawnLocationEvent event) {
        event.setSpawnLocation(this.plugin.getLobbySpawn());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        this.gameManager.findPlayer(event.getPlayer().getUniqueId()).ifPresentOrElse(
                gamePlayer -> {
                    gamePlayer.setPlayer(event.getPlayer());

                    if (gamePlayer.getGame() != null)
                        Bukkit.getScheduler().runTask(this.plugin, () -> gamePlayer.getGame().addPlayer(gamePlayer));

                },
                () -> event.getPlayer().kick()
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);

        UUID uuid = event.getPlayer().getUniqueId();
        ClownChasePlayer gamePlayer = this.gameManager.getPlayers().get(uuid);

        if (gamePlayer == null)
            return;

        this.gameManager.removePlayer(gamePlayer);
    }

}
