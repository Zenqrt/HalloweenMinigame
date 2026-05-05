package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.event.events.GamePlayerJoinEvent;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public final class WaitingGameState extends GameState implements Listener {

    private final ClownChaseGame game;
    private final IntermissionGameState parent;

    public WaitingGameState(IntermissionGameState parent, ClownChaseGame game) {
        this.parent = parent;
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerJoin(GamePlayerJoinEvent event) {
        if (event.getGame() != this.game)
            return;

        if (this.game.getPlayers().size() < this.game.getGameSettings().minPlayers())
            return;

        this.parent.nextState();
    }
}
