package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.event.events.GamePlayerJoinEvent;
import dev.zenqrt.clownchase.event.events.GamePlayerQuitEvent;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameStateSequence;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;

public final class IntermissionGameState extends GameStateSequence implements Listener {

    private static final String PLAYER_JOINED = "game.player_joined";
    private static final String PLAYER_QUIT = "game.player_quit";

    private final ClownChaseGame game;

    public IntermissionGameState(ClownChaseGame game) {
        this.game = game;

        this.states = List.of(
                new WaitingGameState(this, game),
                new CountdownGameState(this, game)
        );
    }

    @Override
    protected void onLastStateEnd() {
        this.game.nextState();
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());
        super.onStateStart();
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);
        super.onStateEnd();
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

    @Override
    public boolean canPlayerJoin() {
        return true;
    }
}
