package dev.zenqrt.clownchase.event.events;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class GamePlayerQuitEvent extends GamePlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final ClownChaseGame game;

    public GamePlayerQuitEvent(ClownChasePlayer gamePlayer, ClownChaseGame game) {
        super(gamePlayer);

        this.game = game;
    }

    public ClownChaseGame getGame() {
        return game;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
