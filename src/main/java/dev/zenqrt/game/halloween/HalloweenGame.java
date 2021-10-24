package dev.zenqrt.game.halloween;

import dev.zenqrt.entity.Candy;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameOptions;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.GameState;
import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.scoreboard.SidebarBuilder;
import dev.zenqrt.timer.countdown.CountdownRunnable;
import dev.zenqrt.timer.countdown.CountdownTask;
import dev.zenqrt.timer.countdown.CountdownTaskBuilder;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.collection.CollectionUtils;
import dev.zenqrt.utils.maze.MazeBuilder;
import dev.zenqrt.utils.particle.ParticleEmitter;
import dev.zenqrt.world.collision.Boundaries;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.utils.time.TimeUnit;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
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

        players.forEach(player -> {
            var playerEntity = player.getPlayer();
            playerEntity.setInstance(instance, findValidSpawn());

            var prefixColor = ParsedColor.of(TextColor.color(210, 77, 255).asHexString());
            var valueColor = ParsedColor.of(TextColor.color(77, 255, 136).asHexString());
            createSidebar(player, prefixColor, valueColor).addViewer(playerEntity);
        });

        var schedulerManager = MinecraftServer.getSchedulerManager();
        var moveListener = createListener(EventListener.builder(PlayerMoveEvent.class).filter(event -> event.getPlayer().getPosition().sameBlock(event.getNewPosition()))
                .handler(event -> event.setCancelled(true)));
        createTask(new CountdownTaskBuilder(schedulerManager, new CountdownRunnable(10, timer -> {
            if(timer == 0) {
                removeListener(moveListener);
                gameTask = createTask(new CountdownTaskBuilder(schedulerManager, new CountdownRunnable(gameTime, this::activeGameTick)));
            }
            if(timer % 10 == 0 || timer <= 5) {
                players.forEach(player -> player.getPlayer().showTitle(Title.title(Component.text("The game starts in", NamedTextColor.YELLOW), Component.text(timer + " seconds", NamedTextColor.RED))));
            }
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

    private Sidebar createSidebar(GamePlayer viewer, String prefixColor, String valueColor) {
        var builder = new SidebarBuilder(MiniMessage.get().parse("<gradient:#ff6600:#ff5050><bold>HALLOWEEN"));
        builder.addLineAtStart(new SidebarBuilder.Line("time_left", MiniMessage.get().parse(prefixColor + "Time left: " + valueColor + gameTime + "s")));
        builder.emptyLineAtStart();
        createLeaderboard(viewer, builder);
        return builder.build();
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
        while (checkSurroundingArea(randomPos)) {
            randomPos = findRandomPosition(random, boundaries);
        }

        return randomPos;
    }

    private boolean checkSurroundingArea(Pos pos) {
        for(double x = 0; x < 2; x++) {
            for(double z = 0; z < 4; z++) {
                for(double y = 0; y < 2; y++) {
                    if(!instance.getBlock(pos.add(x,y,z)).isAir()) return false;
                }
            }
        }

        return true;
    }

    private Pos findRandomPosition(ThreadLocalRandom random, Boundaries boundaries) {
        return new Pos(
                random.nextDouble(boundaries.getMinX(), boundaries.getMaxX()),
                random.nextDouble(boundaries.getMinY(), boundaries.getMaxY()),
                random.nextDouble(boundaries.getMinZ(), boundaries.getMaxZ())
        );
    }

    private void spawnClowns() {

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
