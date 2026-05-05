package dev.zenqrt.clownchase.event.listeners;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

public final class PlayerActivityListeners implements Listener {

    private static final TranslatableComponent KICK_NO_AVAILABLE_GAME = Component.translatable("server.kick.no_available_game", TextColorPresets.ERROR);

    private final GameManager gameManager;
    private final ClownChasePlugin plugin;

    public PlayerActivityListeners(ClownChasePlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        ClownChasePlayer gamePlayer = this.gameManager.addPlayer(event.getUniqueId());

        if (!placeInAvailableGame(gamePlayer))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, KICK_NO_AVAILABLE_GAME);
    }

    private boolean placeInAvailableGame(ClownChasePlayer gamePlayer) {
        Optional<ClownChaseGame> gameOptional = this.gameManager.findAvailableGame();

        if (gameOptional.isEmpty())
            return false;

        ClownChaseGame game = gameOptional.get();
        gamePlayer.setGame(game);
        return true;
//        while (true) {
////            try {
////                return true;
////            } catch (GameAlreadyFullException ex) {
////                this.plugin.getSLF4JLogger().error("Error while adding game player to game {}", game.getId(), ex);
////                this.plugin.getSLF4JLogger().warn("Finding another available game...");
////            }
//        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        this.gameManager.findPlayer(event.getPlayer().getUniqueId()).ifPresentOrElse(
                gamePlayer -> {
                    gamePlayer.setPlayer(event.getPlayer());

                    if (gamePlayer.getGame() != null)
                        Bukkit.getScheduler().runTask(this.plugin, () -> gamePlayer.getGame().tryAddPlayer(gamePlayer));
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
