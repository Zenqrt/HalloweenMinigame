package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.event.events.GamePlayerJoinEvent;
import dev.zenqrt.clownchase.event.events.GamePlayerQuitEvent;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;

public final class IntermissionGameState extends GameState implements Listener {

    private static final String PLAYER_JOINED = "game.player_joined";
    private static final String PLAYER_QUIT = "game.player_quit";

    private final List<GameState> states;
    private int stateIndex;

    private final ClownChaseGame game;

    public IntermissionGameState(ClownChaseGame game) {
        this.game = game;

        this.states = List.of(
                new WaitingGameState(this, game),
                new CountdownGameState(this, game)
        );
        this.stateIndex = 0;
    }

    void nextState() {
        if (this.stateIndex + 1 >= this.states.size()) {
            this.end();
            return;
        }

        GameState currentState = this.states.get(this.stateIndex);
        currentState.end();

        this.states.get(++this.stateIndex).start();
    }

    void previousState() {
        if (this.stateIndex - 1 < 0)
            throw new IndexOutOfBoundsException("state index below 0");

        GameState currentState = this.states.get(this.stateIndex);
        currentState.end();

        this.states.get(--this.stateIndex).start();
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());

        this.states.get(this.stateIndex).start();
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);

        this.states.get(this.stateIndex).end();
    }

    @EventHandler
    public void onGamePlayerJoin(GamePlayerJoinEvent event) {
        if (event.getGame() != this.game)
            return;

        game.audience().sendMessage(Component.translatable(PLAYER_JOINED, TextColorPresets.TEXT,
                Component.text(event.getGamePlayer().validatePlayer().getName(), TextColorPresets.USERNAME)));
    }

    @EventHandler
    public void onGamePlayerQuit(GamePlayerQuitEvent event) {
        if (event.getGame() != this.game)
            return;

        game.audience().sendMessage(Component.translatable(PLAYER_QUIT, TextColorPresets.TEXT,
                Component.text(event.getGamePlayer().validatePlayer().getName(), TextColorPresets.USERNAME)));
    }

}
