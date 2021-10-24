package dev.zenqrt.game;

import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.timer.GenericTaskBuilder;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Game {

    protected final List<GamePlayer> players;
    private final GameOptions gameOptions;
    private final int id;
    private final List<EventListener<? extends PlayerEvent>> listeners;
    private final List<Task> tasks;

    protected GameState state;

    public Game(int id, GameOptions gameOptions) {
        this.id = id;
        this.listeners = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.gameOptions = gameOptions;
        this.players = new ArrayList<>();
        this.state = GameState.WAITING;
    }

    public abstract void init();
    public abstract void startGame();

    public void endGame(Ending ending) {
        new ArrayList<>(listeners).forEach(this::removeListener);
        new ArrayList<>(tasks).forEach(this::removeTask);
    }

    public void join(GamePlayer gamePlayer) {
        gamePlayer.setCurrentGame(this);
        players.add(gamePlayer);
    }

    public void leave(GamePlayer gamePlayer) {
        gamePlayer.setCurrentGame(null);
        players.remove(gamePlayer);
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

    @SuppressWarnings("unchecked")
    public <T extends Task, B extends TaskBuilder> T createTask(B builder) {
        var task = (T) builder.schedule();
        tasks.add(task);
        return task;
    }

    public <T extends Task, B extends GenericTaskBuilder<T, ?>> T createTask(B builder) {
        var task = builder.schedule();
        tasks.add(task);
        return task;
    }

    public void removeTask(Task task) {
        task.cancel();
        tasks.remove(task);
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
