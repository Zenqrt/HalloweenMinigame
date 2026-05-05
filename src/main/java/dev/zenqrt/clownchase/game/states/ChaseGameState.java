package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import dev.zenqrt.clownchase.utils.world.PositionUtils;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public final class ChaseGameState extends GameState implements Listener {

    private static final String DEATH_TITLE = "game.death.title";
    private static final String RESPAWN_COUNTDOWN_TIMER = "game.respawn.timer";
    private static final String RESPAWN_TITLE = "game.respawn.title";

    private final ClownChaseGame game;

    public ChaseGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());
        this.game.getPlayerToClown().forEach((_, clown) -> clown.setNoAi(false));
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!this.game.getPlayers().containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        player.setGameMode(GameMode.SPECTATOR);
        player.setFlySpeed(0);
        player.showTitle(Title.title(
                Component.translatable(DEATH_TITLE, NamedTextColor.RED).decorate(TextDecoration.BOLD),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
        ));

        new RespawnCountdownTask(5, player, this.game.getPlayerToClown().get(player.getUniqueId()))
                .runTaskTimer(this.game.getPlugin(), 0, 20);
    }

    private void respawnPlayer(Player player, Clown assignedClown) {
        BlockPosition spawn;

        do {
            spawn = this.game.findAvailableSpawn();
        } while (PositionUtils.distance(assignedClown.getBukkitEntity().getLocation(), spawn) < 10);

        player.teleport(spawn.toLocation(this.game.getGameWorld()));
        player.setHealth(this.game.getGameSettings().maxHealth());
        player.setGameMode(GameMode.ADVENTURE);
    }


    private class RespawnCountdownTask extends BukkitRunnable {

        private int currentTime;
        private final Clown clown;
        private final Player player;

        RespawnCountdownTask(int time, Player player, Clown clown) {
            this.player = player;
            this.clown = clown;
            this.currentTime = time;
        }

        @Override
        public void run() {
            if (--this.currentTime <= 0) {
                this.player.showTitle(Title.title(
                        Component.translatable(RESPAWN_TITLE, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
                ));
                respawnPlayer(this.player, this.clown);

                this.cancel();
                return;
            }

            this.player.sendActionBar(
                    Component.translatable(RESPAWN_COUNTDOWN_TIMER, TextColorPresets.TEXT, Component.text(currentTime, TextColorPresets.NUMBER))
            );
        }
    }

}
