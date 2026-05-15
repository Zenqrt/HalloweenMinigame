package dev.zenqrt.clownchase.game;

import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.event.events.GamePlayerJoinEvent;
import dev.zenqrt.clownchase.event.events.GamePlayerQuitEvent;
import dev.zenqrt.clownchase.game.base.GameStateSequence;
import dev.zenqrt.clownchase.game.states.*;
import dev.zenqrt.clownchase.lobby.LobbyManager;
import dev.zenqrt.clownchase.map.ClownChaseMap;
import dev.zenqrt.clownchase.map.MapManager;
import dev.zenqrt.clownchase.maze.MazeBoard;
import dev.zenqrt.clownchase.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import dev.zenqrt.clownchase.utils.text.Messages;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class ClownChaseGame extends GameStateSequence implements Listener {

    private static final String KICK_GAME_SHUTDOWN = "server.kick.game_shutdown";
    private static final String CONSUME_CANDY_SHIELD_ALREADY = "game.consume_candy.shield.already";
    private static final String CONSUME_CANDY_SHIELD = "game.consume_candy.shield";
    private static final Sound SHIELD_EQUIP_SOUND = Sound.sound(Key.key("minecraft:item.trident.return"), Sound.Source.MASTER, 1, 1.1F);
    private static final Sound SHIELD_BREAK_SOUND = Sound.sound(Key.key("minecraft:block.glass.break"), Sound.Source.MASTER, 0.75F, 0.8F);
    private static final int MAZE_SCALE = 6;

    private World gameWorld;
    private boolean worldReady;

    private final Map<UUID, Clown> playerToClown = new HashMap<>();
    private final Map<UUID, GamePlayerData> playerData = new HashMap<>();
    private final Map<UUID, ClownChasePlayer> players = new HashMap<>();

    private final ClownChaseMap map;
    private final MazeBoard board;
    private final GameSettings gameSettings;
    private final ClownChasePlugin plugin;
    private final MapManager mapManager;
    private final GameManager gameManager;
    private final int gameId;

    public ClownChaseGame(int gameId, GameManager gameManager, MapManager mapManager, ClownChaseMap map, LobbyManager lobbyManager, ClownChasePlugin plugin, GameSettings gameSettings) {
        this.gameId = gameId;
        this.gameManager = gameManager;
        this.mapManager = mapManager;
        this.map = map;
        this.plugin = plugin;
        this.gameSettings = gameSettings;
        this.board = new MazeBoard(16, 16);

        this.states = List.of(
                new SetupWorldGameState(this, mapManager, new RecursiveDivisionStrategy(), MAZE_SCALE),
                new IntermissionGameState(this),
                new EnsureWorldReadyGameState(this),
                new SetupPlayersGameState(this),
                new SpawnClownsGameState(this),
                new FreezeCountdownGameState(this),
                new ChaseGameState(this),
                new AnnounceWinnerGameState(this, 200), // 10 seconds
                new TeleportPlayersToLobbyGameState(this, gameManager, lobbyManager)
        );
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);

        super.onStateStart();
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        this.players.values().stream()
                .map(ClownChasePlayer::validatePlayer)
                .filter(player -> player.getWorld().equals(this.gameWorld))
                .forEach(player -> player.kick(Component.translatable(KICK_GAME_SHUTDOWN, NamedTextColor.RED)));
        this.players.clear();

        this.gameManager.deleteGame(gameId);
        tryDeleteGameWorld();
    }

    private void tryDeleteGameWorld() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ClownChaseGame.this.mapManager.deleteGameWorld(gameId, gameWorld);
                    this.cancel();
                } catch (RuntimeException ex) {
                    ClownChaseGame.this.plugin.getSLF4JLogger().error("Failed to delete game world '{}': {}\nRetrying...", gameWorld.getName(), ex.getMessage());
                }

            }
        }.runTaskTimer(this.plugin, 20, 40);
    }

    @EventHandler
    public void onGamePlayerQuit(GamePlayerQuitEvent event) {
        UUID uuid = event.getGamePlayer().getUniqueId();
        Clown clown = playerToClown.remove(uuid);

        if (clown == null)
            return;

        clown.remove(Entity.RemovalReason.DISCARDED);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ClownChasePlayer gamePlayer = this.players.get(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        this.gameManager.leaveGame(gamePlayer, this);
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

    public void addPlayer(ClownChasePlayer gamePlayer) {
        // TODO: Instead of this, make a synchronous lock to prevent this issue in the first place
        this.players.put(gamePlayer.getUniqueId(), gamePlayer);
        this.playerData.put(gamePlayer.getUniqueId(), new GamePlayerData());

        GamePlayerJoinEvent joinEvent = new GamePlayerJoinEvent(gamePlayer, this);
        Bukkit.getPluginManager().callEvent(joinEvent);
    }

    public boolean removePlayer(ClownChasePlayer gamePlayer) {
        this.players.remove(gamePlayer.getUniqueId(), gamePlayer);
        this.playerData.remove(gamePlayer.getUniqueId());

        GamePlayerQuitEvent quitEvent = new GamePlayerQuitEvent(gamePlayer, this);
        Bukkit.getPluginManager().callEvent(quitEvent);

        return true;
    }

    public boolean canPlayerJoin() {
        return getCurrentState().canPlayerJoin() && this.players.size() < this.gameSettings.maxPlayers();
    }

    public void grantShield(Player player, GamePlayerData playerData) {
        if (playerData.isShielded()) {
            player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_SHIELD_ALREADY)));
            return;
        }

        playerData.setShielded(true);
        player.playSound(SHIELD_EQUIP_SOUND, Sound.Emitter.self());
        player.sendMessage(Messages.consumeSpecialCandy(Component.translatable(CONSUME_CANDY_SHIELD, Component.text("1 hit", TextColorPresets.NUMBER))));
    }

    public void breakShield(Player player, GamePlayerData playerData) {
        playerData.setShielded(false);

        this.audience().playSound(SHIELD_BREAK_SOUND, player.getX(), player.getY(), player.getZ());
    }

    /**
     * todo: Please remove this after replacing this method. The better method will be placed in MazeUtils
     */
    @SuppressWarnings("UnstableApiUsage")
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

    @SuppressWarnings("UnstableApiUsage")
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

    public void clearClowns() {
        playerToClown.clear();
    }

    public Map<UUID, Clown> getPlayerToClown() {
        return Collections.unmodifiableMap(playerToClown);
    }

    public Map<UUID, ClownChasePlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public List<LeaderboardEntry> getCandyLeaderboard(int limit) {
        return this.players.entrySet().stream()
                .map(entry -> new LeaderboardEntry(entry.getValue(), this.getPlayerData(entry.getKey())))
                .sorted(Comparator.comparingInt((LeaderboardEntry entry) -> entry.playerData.getCandyCollected()).reversed())
                .limit(limit)
                .toList();
    }

    public MazeTheme<?, ?> getTheme() {
        return map.mazeTheme();
    }

    public ClownChaseMap getMap() {
        return map;
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

    public boolean isWorldReady() {
        return worldReady;
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

    public record LeaderboardEntry(ClownChasePlayer gamePlayer, GamePlayerData playerData) {}
}
