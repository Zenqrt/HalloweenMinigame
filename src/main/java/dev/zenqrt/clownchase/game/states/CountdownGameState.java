package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.event.events.GamePlayerQuitEvent;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public final class CountdownGameState extends GameState implements Listener {

    private static final TranslatableComponent CANCELLED_NOT_ENOUGH_PLAYERS = Component.translatable("game.countdown.cancelled_not_enough_players", TextColorPresets.ERROR);
    private static final String START_COUNTDOWN_TIMER = "game.countdown.start_timer";

    private BukkitTask countdownTask;

    private final ClownChaseGame game;
    private final IntermissionGameState parent;

    public CountdownGameState(IntermissionGameState parent, ClownChaseGame game) {
        this.parent = parent;
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());

        this.countdownTask = Bukkit.getScheduler().runTaskTimer(this.game.getPlugin(), new CountdownTask(15), 0, 20);
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);

        if (!countdownTask.isCancelled())
            countdownTask.cancel();
    }

    @EventHandler
    public void onGamePlayerQuit(GamePlayerQuitEvent event) {
        if (event.getGame() != this.game)
            return;

        if (this.game.getPlayers().size() < this.game.getGameSettings().minPlayers()) {
            this.game.audience().sendMessage(CANCELLED_NOT_ENOUGH_PLAYERS);
            this.parent.previousState();
        }
    }

    private class CountdownTask implements Runnable {

        private int currentTime;

        CountdownTask(int time) {
            this.currentTime = time;
        }

        @Override
        public void run() {
            if (--currentTime <= 0) {
                CountdownGameState.this.parent.nextState();
                return;
            }

            CountdownGameState.this.game.audience().sendActionBar(
                    Component.translatable(START_COUNTDOWN_TIMER, TextColorPresets.TEXT, Component.text(currentTime, TextColorPresets.NUMBER))
            );
        }
    }

}
