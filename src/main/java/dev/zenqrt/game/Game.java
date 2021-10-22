package dev.zenqrt.game;

import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.server.MinestomServer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Game {

    protected final List<GamePlayer> players;
    private final GameOptions gameOptions;
    private final int id;
    private final List<EventListener<? extends PlayerEvent>> listeners;

    protected GameState state;

    public Game(int id, GameOptions gameOptions) {
        this.id = id;
        this.listeners = new ArrayList<>();
        this.gameOptions = gameOptions;
        this.players = new ArrayList<>();
        this.state = GameState.WAITING;
    }

    public abstract void startGame();

    public void endGame(Ending ending) {
        new ArrayList<>(listeners).forEach(this::removeListener);
    }

    public <T extends PlayerEvent> EventListener<T> createListener(EventListener.Builder<T> builder) {
        var listener = builder.filter(event -> MinestomServer.getGameManager().findGamePlayer(event.getPlayer()).getCurrentGame() == this).build();
        MinecraftServer.getGlobalEventHandler().addListener(listener);
        listeners.add(listener);

        return listener;
    }

    public <T extends PlayerEvent> EventListener<T> createListener(Class<T> eventType, Consumer<T> function) {
        return createListener(EventListener.builder(eventType).handler(function));
    }

    public void removeListener(EventListener<? extends PlayerEvent> listener) {
        MinecraftServer.getGlobalEventHandler().removeListener(listener);
        listeners.remove(listener);
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public int getId() {
        return id;
    }
}
