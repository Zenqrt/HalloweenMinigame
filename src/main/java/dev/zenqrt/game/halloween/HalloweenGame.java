package dev.zenqrt.game.halloween;

import dev.zenqrt.entity.Candy;
import dev.zenqrt.entity.monster.KillerClown;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameOptions;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.GameState;
import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.game.halloween.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.game.halloween.maze.themes.HedgeMazeTheme;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.game.timers.CountdownTimerTask;
import dev.zenqrt.scoreboard.SidebarBuilder;
import dev.zenqrt.timer.countdown.CountdownRunnable;
import dev.zenqrt.timer.countdown.CountdownTask;
import dev.zenqrt.timer.countdown.CountdownTaskBuilder;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.collection.CollectionUtils;
import dev.zenqrt.utils.maze.MazeBuilder;
import dev.zenqrt.world.collision.Boundaries;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HalloweenGame extends Game {

    public static final Team PLAYER_TEAM = MinecraftServer.getTeamManager().createBuilder("players")
            .teamColor(NamedTextColor.AQUA)
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build();

    private final List<Candy> candies;
    private final Map<GamePlayer, Integer> candiesCollected;
    private final Instance instance;
    private final int gameTime;

    private Boundaries boundaries;
    private CountdownTask gameTask;

    public HalloweenGame(int id, Instance instance) {
        super(id, new GameOptions.Builder()
                .minPlayers(2)
                .maxPlayers(8)
                .canJoinInGame(false)
                .build()
        );

        this.instance = instance;
        this.candies = new ArrayList<>();
        this.candiesCollected = new LinkedHashMap<>();
        this.gameTime = 130;
    }

    @Override
    public void init() {
        generateMaze(new Pos(0, 42, 0), new RecursiveDivisionStrategy(), new HedgeMazeTheme(6, 4 ,5));
    }

    /*
        CLOWN CHASE
        --------------
        Time left: 3:10

        Qefib: 5
        Bemptay: 4
        legocmonster: 3
        ...
        Walmqrt: 2
         */
    @Override
    public void startGame() {
        state = GameState.IN_GAME;

        var prefixColor = ParsedColor.of(TextColor.color(210, 77, 255).asHexString());
        var valueColor = ParsedColor.of(TextColor.color(77, 255, 136).asHexString());

        players.forEach(player -> {
            var playerEntity = player.getPlayer();
            var position = findValidSpawn();
            playerEntity.setInstance(instance, position);
            spawnClown(playerEntity, position);
            createGameSidebar(player, prefixColor, valueColor).addViewer(playerEntity);
        });

        var schedulerManager = MinecraftServer.getSchedulerManager();
        var moveListener = createListener(EventListener.builder(PlayerMoveEvent.class).filter(event -> event.getPlayer().getPosition().sameBlock(event.getNewPosition()))
                .handler(event -> event.setCancelled(true)));

        createTask(new CountdownTaskBuilder(schedulerManager, new CountdownTimerTask(10, Audience.audience(players),
                prefixColor + "The game starts in <yellow>%d " + prefixColor + "seconds!", () ->
        {
            removeListener(moveListener);
            gameTask = createTask(new CountdownTaskBuilder(schedulerManager, new CountdownRunnable(gameTime, this::activeGameTick)));
        })).repeat(1, TimeUnit.SECOND));

    }

    private void activeGameTick(int time) {
        for(GamePlayer gamePlayer : players) {
            for(Candy candy : candies) {
                var playerEntity = gamePlayer.getPlayer();
                if(!playerEntity.getBoundingBox().intersect(candy)) continue;

                candy.consume(playerEntity);
            }
        }
    }

    private Sidebar createLobbySidebar(GamePlayer viewer, String prefixColor, String valueColor) {
        var builder = createSidebarBuilder();
        builder.addLineAtStart(new SidebarBuilder.Line("game_state", MiniMessage.get().parse(prefixColor + "Waiting...")));
        return null;
    }

    private Sidebar createGameSidebar(GamePlayer viewer, String prefixColor, String valueColor) {
        var builder = createSidebarBuilder();
        builder.addLineAtStart(new SidebarBuilder.Line("time_left", MiniMessage.get().parse(prefixColor + "Time left: " + valueColor + gameTime + "s")));
        builder.emptyLineAtStart();
        createLeaderboard(viewer, builder);
        return builder.build();
    }

    private SidebarBuilder createSidebarBuilder() {
        return new SidebarBuilder(MiniMessage.get().parse("<gradient:#ff6600:#ff5050><bold>CLOWN CHASE")); // idk a name
    }

    private void createLeaderboard(GamePlayer viewer, SidebarBuilder builder) {
        var leaderboard = CollectionUtils.sortByValue(candiesCollected, Comparator.reverseOrder()).entrySet()
                .stream()
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int index = 0;
        for(var entry : leaderboard.entrySet()) {
            index++;

            builder.addLineAtStart(new SidebarBuilder.Line("leaderboard_" + index, getLeaderboardFormat(entry.getKey(), entry.getValue())));
        }

        if(!leaderboard.containsKey(viewer)) {
            builder.addLineAtStart(new SidebarBuilder.Line("leaderboard_space", Component.text("...")));
            builder.addLineAtStart(new SidebarBuilder.Line("leaderboard_self", getLeaderboardFormat(viewer, candiesCollected.get(viewer))));
        }
    }

    private Component getLeaderboardFormat(GamePlayer gamePlayer, int candies) {
        return MiniMessage.get().parse("<aqua>" + gamePlayer.getPlayer().getName() + "<reset>: <green>" + candies + "♧");
    }

    private Pos findValidSpawn() {
        if(boundaries == null) throw new NullPointerException("Boundaries cannot be null");

        var random = ThreadLocalRandom.current();
        var randomPos = findRandomPosition(random, boundaries);
        while (checkSurroundingArea(randomPos, 2, 2, 2)) {
            randomPos = findRandomPosition(random, boundaries);
        }

        return randomPos;
    }

    private boolean checkSurroundingArea(Pos pos, int xArea, int yArea, int zArea) {
        for(double x = 0; x < xArea; x++) {
            for(double z = 0; z < zArea; z++) {
                for(double y = 0; y < yArea; y++) {
                    if(!instance.getBlock(pos.add(x,y,z)).isAir()) return false;
                }
            }
        }

        return true;
    }

    @Nullable
    private Pos findValidPositionInArea(Pos pos, int xArea, int yArea, int zArea) {
        for(double x = 0; x < xArea; x++) {
            for(double z = 0; z < zArea; z++) {
                for(double y = 0; y < yArea; y++) {
                    if(!instance.getBlock(pos.add(x,y,z)).isAir()) continue;
                    return pos.add(x, 0, z);
                }
            }
        }
        return null;
    }

    private Pos findRandomPosition(ThreadLocalRandom random, Boundaries boundaries) {
        return new Pos(
                random.nextDouble(boundaries.getMinX(), boundaries.getMaxX()),
                random.nextDouble(boundaries.getMinY(), boundaries.getMaxY()),
                random.nextDouble(boundaries.getMinZ(), boundaries.getMaxZ())
        );
    }

    private void spawnClown(Player player, Pos position) {
        var randomPos = findValidPositionInArea(position, 2, 2, 2);
        if(randomPos == null) {
            System.out.println("uh oh");
            return;
        }

        var clown = new KillerClown(new FakePlayerOption(), null);
        clown.setTarget(player);
    }

    private void generateMaze(Pos pos, MazeGenerationStrategy strategy, MazeTheme<?,?> theme) {
        var mazeBoard = new MazeBoard(16, 16);
        strategy.execute(mazeBoard);

        this.boundaries = MazeBuilder.constructMaze(mazeBoard, theme, 6, instance, pos);
    }

    @Override
    public void endGame(Ending ending) {

    }
}
