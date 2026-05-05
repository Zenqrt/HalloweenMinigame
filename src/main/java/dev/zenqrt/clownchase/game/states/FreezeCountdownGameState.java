package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public final class FreezeCountdownGameState extends GameState implements Listener {

    private static final String MOVE_COUNTDOWN_TIMER = "game.countdown.move_timer";
    private static final String MOVE_COUNTDOWN_TIMER_TITLE = "game.countdown.move_timer.title";
    private static final String MOVE_COUNTDOWN_TIMER_SUBTITLE = "game.countdown.move_timer.subtitle";
    private static final String GAME_START_TITLE = "game.start.title";
    private static final AttributeModifier FREEZE_MODIFIER = new AttributeModifier(NamespacedKey.minecraft("freeze"), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    private BukkitTask countdownTask;
    private final ClownChaseGame game;

    public FreezeCountdownGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());

        this.game.getPlayers().forEach((_, gamePlayer) ->
                gamePlayer.validatePlayer().getAttribute(Attribute.MOVEMENT_SPEED).addTransientModifier(FREEZE_MODIFIER));

        this.countdownTask = Bukkit.getScheduler().runTaskTimer(this.game.getPlugin(), new CountdownTask(10), 0, 20);
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);

        if (!this.countdownTask.isCancelled())
            this.countdownTask.cancel();

        this.game.getPlayers().forEach((_, gamePlayer) ->
                gamePlayer.validatePlayer().getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(FREEZE_MODIFIER));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition())
            return;

        event.setTo(event.getFrom());
    }

    private class CountdownTask implements Runnable {

        private int currentTime;

        CountdownTask(int time) {
            this.currentTime = time;
        }

        @Override
        public void run() {
            Audience players = FreezeCountdownGameState.this.game.audience();

            if (--currentTime <= 0) {
                players.showTitle(Title.title(
                        Component.translatable(GAME_START_TITLE, NamedTextColor.RED).decorate(TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
                ));
                FreezeCountdownGameState.this.game.nextState();
                return;
            }

            players.sendActionBar(
                    Component.translatable(MOVE_COUNTDOWN_TIMER, TextColorPresets.TEXT, Component.text(currentTime, TextColorPresets.NUMBER))
            );

            if (currentTime <= 5) {
                players.showTitle(Title.title(
                        Component.translatable(MOVE_COUNTDOWN_TIMER_TITLE, TextColorPresets.NUMBER, Component.text(currentTime)).decorate(TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)
                ));
            }
        }
    }
}
