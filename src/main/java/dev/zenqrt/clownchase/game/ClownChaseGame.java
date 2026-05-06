package dev.zenqrt.clownchase.game;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.event.events.GamePlayerJoinEvent;
import dev.zenqrt.clownchase.exceptions.GameAlreadyFullException;
import dev.zenqrt.clownchase.exceptions.GamePlayerAlreadyInGameException;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.game.states.*;
import dev.zenqrt.clownchase.maze.MazeBoard;
import dev.zenqrt.clownchase.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class ClownChaseGame extends GameState {

    private static final int MAZE_SCALE = 6;
    private final List<GameState> states;
    private int stateIndex;

    private World gameWorld;
    private boolean worldReady;

    private final Map<UUID, Clown> playerToClown = new HashMap<>();

    private final Map<UUID, GamePlayerData> playerData = new HashMap<>();
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
                new SetupWorldGameState(this, new RecursiveDivisionStrategy(), MAZE_SCALE),
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

    public GamePlayerData getPlayerData(UUID uuid) {
        GamePlayerData data = this.playerData.get(uuid);

        if (data == null)
            throw new IllegalStateException("Player data missing for " + uuid);

        return data;
    }

    public Audience audience() {
        return Audience.audience(players.values());
    }

    public void tryAddPlayer(ClownChasePlayer gamePlayer) {
        // TODO: Instead of this, make a synchronous lock to prevent this issue in the first place
        if (this.players.containsKey(gamePlayer.getUniqueId()))
            throw new GamePlayerAlreadyInGameException(gamePlayer);

        if (this.players.size() >= this.gameSettings.maxPlayers())
            throw new GameAlreadyFullException(this);

        this.players.put(gamePlayer.getUniqueId(), gamePlayer);
        this.playerData.put(gamePlayer.getUniqueId(), new GamePlayerData());

        GamePlayerJoinEvent joinEvent = new GamePlayerJoinEvent(gamePlayer, this);
        Bukkit.getPluginManager().callEvent(joinEvent);
    }

    public boolean removePlayer(ClownChasePlayer gamePlayer) {
        return this.players.remove(gamePlayer.getUniqueId(), gamePlayer) || this.playerData.remove(gamePlayer.getUniqueId()) != null;
    }

    public boolean canPlayerJoin() {
        return this.worldReady && this.players.size() < this.gameSettings.maxPlayers();
    }

    /**
     * todo: Please remove this after replacing this method. The better method will be placed in MazeUtils
     */
    public BlockPosition findAvailableSpawn(int xRadius, int zRadius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int xMax = this.board.getDimensionX() * MAZE_SCALE;
        int yMax = this.board.getDimensionY() * MAZE_SCALE;

        BlockPosition position;

        do {
            position = Position.block(random.nextInt(xMax), 42, random.nextInt(yMax));
        } while (!isSurroundingAreaOpen(this.gameWorld, position, xRadius, zRadius));

        return position;
    }

    private static boolean isSurroundingAreaOpen(World world, BlockPosition origin, int xArea, int zArea) {
        for (int x = -xArea; x <= xArea; x++) {
            for (int z = -zArea; z <= zArea; z++) {
                BlockPosition position = origin.offset(x, 0, z);

                if (!world.getBlockAt(position.blockX(), position.blockY(), position.blockZ()).isEmpty())
                    return false;
            }
        }
        return true;
    }

    public void assignClown(ClownChasePlayer gamePlayer, Clown clown) {
        playerToClown.put(gamePlayer.getUniqueId(), clown);
    }

    public void unassignClown(ClownChasePlayer gamePlayer) {
        playerToClown.remove(gamePlayer);
    }

    public Map<UUID, Clown> getPlayerToClown() {
        return Collections.unmodifiableMap(playerToClown);
    }

    public Map<UUID, ClownChasePlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public MazeTheme<?, ?> getTheme() {
        return theme;
    }

    public MazeBoard getBoard() {
        return board;
    }

    public void setGameWorld(World gameWorld) {
        this.gameWorld = gameWorld;
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public void setWorldReady(boolean worldReady) {
        this.worldReady = worldReady;
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
