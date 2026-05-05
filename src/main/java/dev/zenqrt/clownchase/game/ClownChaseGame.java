package dev.zenqrt.clownchase.game;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.event.events.GamePlayerJoinEvent;
import dev.zenqrt.clownchase.exceptions.GameAlreadyFullException;
import dev.zenqrt.clownchase.exceptions.GamePlayerAlreadyInGameException;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.game.states.*;
import dev.zenqrt.clownchase.maze.MazeBoard;
import dev.zenqrt.clownchase.maze.MazeBuilder;
import dev.zenqrt.clownchase.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.clownchase.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import dev.zenqrt.clownchase.utils.maze.MazeUtils;
import dev.zenqrt.clownchase.world.generator.VoidGenerator;
import io.papermc.paper.math.Position;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.*;

public final class ClownChaseGame extends GameState {

    private static final MazeGenerationStrategy MAZE_GENERATION_STRATEGY = new RecursiveDivisionStrategy();
    private final List<GameState> states;
    private int stateIndex;

    private World gameWorld;
    private boolean worldReady;

    private final Map<ClownChasePlayer, Clown> clowns = new HashMap<>();

    private final Map<UUID, ClownChasePlayer> players = new HashMap<>();
    private final MazeTheme<?, ?> theme;
    private final MazeBoard board;
    private final GameSettings gameSettings;
    private final ClownChasePlugin plugin;
    private final int gameId;

    public ClownChaseGame(int gameId, ClownChasePlugin plugin, MazeTheme<?, ?> theme, GameSettings gameSettings) {
        this.gameId = gameId;
        this.plugin = plugin;
        this.gameSettings = gameSettings;
        this.board = new MazeBoard(16, 16);
        this.theme = theme;

        this.states = List.of(
                new IntermissionGameState(this),
                new SetupPlayersGameState(this),
                new SpawnClownsGameState(this),
                new FreezeCountdownGameState(this),
                new ChaseGameState(this)
        );
        this.stateIndex = 0;
    }

    @Override
    protected void onStateStart() {
        // Generate world  -----------    TODO: Make world gen async if possible
        this.gameWorld = WorldCreator.name("clown-chase_" + this.gameId)
                .generator(new VoidGenerator())
                .createWorld();

        if (this.gameWorld == null)
            throw new NullPointerException("gameWorld");

        this.gameWorld.setAutoSave(false);
        this.gameWorld.setGameRule(GameRules.SPAWN_MOBS, false);
        this.gameWorld.setGameRule(GameRules.ADVANCE_TIME, false);
        this.gameWorld.setGameRule(GameRules.ADVANCE_WEATHER, false);
        this.gameWorld.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
        this.gameWorld.setGameRule(GameRules.FALL_DAMAGE, false);
        this.gameWorld.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);

        // Generate maze
        MAZE_GENERATION_STRATEGY.execute(this.board);

        MazeUtils.printMaze(this.board);
        MazeBuilder.constructMaze(this.board, this.theme, 6, this.gameWorld, Position.block(0, 42, 0));

        this.worldReady = true;
        this.states.get(stateIndex).start();
    }

    @Override
    protected void onStateEnd() {
        this.states.get(stateIndex).end();
    }

    public void nextState() {
        if (stateIndex + 1 >= states.size()) {
            this.end();
            return;
        }

        GameState currentState = states.get(stateIndex);
        currentState.end();

        states.get(++stateIndex).start();
    }

    public void previousState() {
        if (stateIndex - 1 < 0)
            throw new IndexOutOfBoundsException("state index below 0");

        GameState currentState = states.get(stateIndex);
        currentState.end();

        states.get(--stateIndex).start();
    }

    public Audience audience() {
        return Audience.audience(players.values());
    }

    public void tryAddPlayer(ClownChasePlayer gamePlayer) {
        if (this.players.containsKey(gamePlayer.getUniqueId()))
            throw new GamePlayerAlreadyInGameException(gamePlayer);

        if (this.players.size() >= this.gameSettings.maxPlayers())
            throw new GameAlreadyFullException(this);

        this.players.put(gamePlayer.getUniqueId(), gamePlayer);

        GamePlayerJoinEvent joinEvent = new GamePlayerJoinEvent(gamePlayer, this);
        Bukkit.getPluginManager().callEvent(joinEvent);
    }

    public boolean removePlayer(ClownChasePlayer gamePlayer) {
        return this.players.remove(gamePlayer.getUniqueId(), gamePlayer);
    }

    public boolean canPlayerJoin() {
        return this.players.size() < this.gameSettings.maxPlayers();
    }

    public void assignClown(ClownChasePlayer gamePlayer, Clown clown) {
        clowns.put(gamePlayer, clown);
    }

    public void unassignClown(ClownChasePlayer gamePlayer) {
        clowns.remove(gamePlayer);
    }

    public Map<ClownChasePlayer, Clown> getClowns() {
        return Collections.unmodifiableMap(clowns);
    }

    public Map<UUID, ClownChasePlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public MazeBoard getBoard() {
        return board;
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public GameState getCurrentState() {
        return this.states.get(this.stateIndex);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public ClownChasePlugin getPlugin() {
        return plugin;
    }

    public int getId() {
        return gameId;
    }
}
