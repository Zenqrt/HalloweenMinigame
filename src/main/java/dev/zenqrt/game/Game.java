package dev.zenqrt.game;

import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.timers.CountdownTimerTask;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.timer.MinestomRunnable;
import dev.zenqrt.utils.Utils;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

public abstract class Game {

    protected final Map<Player, GamePlayer> players;
    private final GameOptions gameOptions;
    private final int id;
    private final List<EventListener<? extends PlayerEvent>> listeners;
    private final Map<String, Task> tasks;

    protected GameState state;

    public Game(int id, GameOptions gameOptions) {
        this.id = id;
        this.listeners = new ArrayList<>();
        this.tasks = new HashMap<>();
        this.gameOptions = gameOptions;
        this.players = new HashMap<>();
        this.state = GameState.WAITING;
    }

    public abstract void init();
    public abstract void startGame();

    public void startCountdown(int seconds) {
        var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
        mapTask("countdown", new CountdownTimerTask(seconds,
                timer -> Audience.audience(getPlayers()).sendActionBar(MiniMessage.get().parse(String.format(color + "The game will start in <yellow%d " + color + "seconds!", timer))),
                this::startGame).repeat(Duration.of(1, TimeUnit.SECOND)).schedule());
    }

    public void endGame(Ending ending) {
        new ArrayList<>(listeners).forEach(this::removeListener);
        new HashMap<>(tasks).forEach((key, task) -> {
            task.cancel();
            tasks.remove(key);
        });
    }

    public void join(GamePlayer gamePlayer) {
        gamePlayer.setCurrentGame(this);
        players.put(gamePlayer.getPlayer(), gamePlayer);
    }

    public void leave(GamePlayer gamePlayer) {
        gamePlayer.setCurrentGame(null);
        leave(gamePlayer.getPlayer());
    }

    public void leave(Player player) {
        players.remove(player);
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

    public <T extends Task> void mapTask(String key, T task) {
        Utils.putOrReplace(tasks, key, task);
    }

    public void unmapTask(String key) {
        tasks.get(key).cancel();
        tasks.remove(key);
    }

    public GamePlayer getPlayer(Player player) {
        return players.get(player);
    }

    public Collection<GamePlayer> getPlayers() {
        return players.values();
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public int getId() {
        return id;
    }
}
