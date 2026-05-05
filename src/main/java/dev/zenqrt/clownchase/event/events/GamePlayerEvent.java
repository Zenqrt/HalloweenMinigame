package dev.zenqrt.clownchase.event.events;

import dev.zenqrt.clownchase.game.ClownChasePlayer;
import org.bukkit.event.Event;

public abstract class GamePlayerEvent extends Event {

    protected final ClownChasePlayer gamePlayer;

    protected GamePlayerEvent(ClownChasePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public ClownChasePlayer getGamePlayer() {
        return gamePlayer;
    }
}
