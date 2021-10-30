package dev.zenqrt.game.halloween;

import dev.zenqrt.entity.Candy;
import dev.zenqrt.entity.monster.KillerClown;
import dev.zenqrt.entity.player.FakePlayerEntity;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameOptions;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.GameState;
import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.halloween.ending.HalloweenEnding;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.game.halloween.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.game.halloween.maze.themes.HedgeMazeTheme;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.game.timers.CountdownTimerTask;
import dev.zenqrt.scoreboard.SidebarBuilder;
import dev.zenqrt.timer.countdown.CountdownRunnable;
import dev.zenqrt.utils.Utils;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.collection.CollectionUtils;
import dev.zenqrt.utils.maze.MazeBuilder;
import dev.zenqrt.world.collision.Boundaries;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HalloweenGame extends Game {

    private static final String PREFIX_COLOR = ParsedColor.of(TextColor.color(210, 77, 255).asHexString());
    private static final String VALUE_COLOR = ParsedColor.of(TextColor.color(77, 255, 136).asHexString());
    public static final Team PLAYER_TEAM = MinecraftServer.getTeamManager().createBuilder("players")
            .teamColor(NamedTextColor.AQUA)
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build();

    private final List<Candy> candies;
    private final List<Player> respawning;
    private final Map<GamePlayer, Integer> candiesCollected;
    private final Map<GamePlayer, Sidebar> sidebarDisplay;
    private final Map<GamePlayer, KillerClown> clowns;
    private final Instance instance;
    private final int gameTime;
    private final PlainTextComponentSerializer plainText;
    private final HalloweenEnding ending;
    private BossBar bossBar;

    private Boundaries boundaries;

    public HalloweenGame(int id, Instance instance) {
        super(id, new GameOptions.Builder()
                .minPlayers(2)
                .maxPlayers(8)
                .canJoinInGame(false)
                .build()
        );

        this.instance = instance;
        this.candies = new ArrayList<>();
        this.respawning = new ArrayList<>();
        this.candiesCollected = new LinkedHashMap<>();
        this.sidebarDisplay = new HashMap<>();
        this.clowns = new HashMap<>();
        this.gameTime = 130;
        this.plainText = PlainTextComponentSerializer.plainText();
        this.ending = new HalloweenEnding();
    }

    @Override
    public void init() {
        generateMaze(new Pos(0, 42, 0), new RecursiveDivisionStrategy(), new HedgeMazeTheme(6, 4 ,5));
    }

    /*
        CLOWN CHASE
        --------------
        Qefib: 5
        Bemptay: 4
        legocmonster: 3
        ...
        Walmqrt: 2

        Distance from clown: 10m
         */
    @Override
    public void startGame() {
        state = GameState.IN_GAME;

        var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
        this.bossBar = BossBar.bossBar(MiniMessage.get().parse(color + "Time left: <aqua>" + gameTime + color + "s"), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        getPlayers().forEach(this::initPlayer);

        var moveListener = createPlayerListener(EventListener.builder(PlayerMoveEvent.class).filter(event -> !event.getNewPosition().samePoint(event.getPlayer().getPosition()))
                .handler(event -> event.setCancelled(true)));

        mapTask("movement_countdown", new CountdownTimerTask(10,
                timer -> Audience.audience(getPlayers()).sendActionBar(MiniMessage.get().parse(String.format(PREFIX_COLOR + "You can move in <yellow>%d " + PREFIX_COLOR + "seconds!", timer))), () -> {
            removeListener(moveListener);
            createEntityListener(EventListener.builder(EntityDamageEvent.class)
                    .handler(event -> {
                        final var player = (Player) event.getEntity();

                        if(respawning.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }

                        if(event.getEntity().getHealth() - event.getDamage() <= 0) {
                            var gamePlayer = getPlayer(player);
                            var clown = clowns.get(gamePlayer);
                            event.setCancelled(true);
                            respawning.add(player);
                            clown.setAttacking(false);
                            removeCandy(gamePlayer, getCandy(gamePlayer) / 2);
                            player.setHealth(player.getMaxHealth());
                            player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 100));
                            player.showTitle(Title.title(Component.text("YOU DIED!", NamedTextColor.RED).decorate(TextDecoration.BOLD), Component.empty()));
                            player.playSound(Sound.sound(SoundEvent.ENTITY_ZOMBIE_DEATH, Sound.Source.PLAYER, 1, 0));


                            var deadBody = new FakePlayerEntity(UUID.randomUUID(), "", new FakePlayerOption(), fakePlayer -> {
                                fakePlayer.setInstance(Objects.requireNonNull(player.getInstance()), player.getPosition());
                                fakePlayer.setSkin(player.getSkin());
                                fakePlayer.setPose(Entity.Pose.SLEEPING);
                            });
                            deadBody.enableSkinLayers();

                            new CountdownRunnable(5) {
                                @Override
                                public void beforeIncrement() {
                                    player.sendActionBar(MiniMessage.get().parse(String.format(PREFIX_COLOR + "Respawning in <yellow>%d " + PREFIX_COLOR + "seconds...", timer)));
                                }

                                @Override
                                public void endCountdown() {
                                    respawning.remove(player);
                                    respawnPlayer(player, findValidSpawn());
                                    clown.setAttacking(true);
                                    player.removeEffect(PotionEffect.BLINDNESS);
                                    player.showTitle(Title.title(Component.text("RESPAWNED!", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD), Component.empty()));
                                }
                            }.repeat(Duration.of(1, TimeUnit.SECOND)).schedule();
                        }
                    })
            );
            toggleClownAttacks(true);
            mapTask("game_timer", new CountdownRunnable(gameTime) {

                @Override
                public void beforeIncrement() {
                    bossBar.name(MiniMessage.get().parse(color + "Time left: <aqua>" + timer + color + "s"));
                    bossBar.progress((float) timer / (float) gameTime);
                    updateSidebar("distance", gamePlayer -> MiniMessage.get().parse(PREFIX_COLOR + "Distance from clown: " + VALUE_COLOR + getDistanceFromClown(gamePlayer) + "m"));
                }

                @Override
                public void endCountdown() {
                    endGame(null);
                }
            }.repeat(Duration.of(1, TimeUnit.SECOND)).schedule());

            mapTask("game_tick", MinecraftServer.getSchedulerManager().buildTask(this::activeGameTick)
                    .repeat(Duration.of(1, TimeUnit.SECOND)).schedule());
        }).repeat(Duration.of(1, TimeUnit.SECOND)).schedule());
    }

    private void initPlayer(GamePlayer gamePlayer) {
        var player = gamePlayer.getPlayer();
        var position = findValidSpawn();
        respawnPlayer(player, position);
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(10);
        player.setHealth(10);

        Utils.putOrReplace(candiesCollected, gamePlayer, 0);

        spawnClown(gamePlayer, position.add(0,1,0));
        displaySidebar(gamePlayer, createGameSidebar(gamePlayer, PREFIX_COLOR, VALUE_COLOR));
        player.showBossBar(bossBar);
    }

    private void respawnPlayer(Player player, Point position) {
        var pos = position.add(0,1,0);
        if(player.getInstance() == null || !player.getInstance().equals(instance)) {
            player.setInstance(instance, pos);
        } else {
            player.teleport(Pos.fromPoint(pos));
        }
    }

    private void toggleClownAttacks(boolean attacking) {
        clowns.forEach((player, clown) -> clown.setAttacking(attacking));
    }

    private void activeGameTick() {
        for(GamePlayer gamePlayer : getPlayers()) {
            var remove = new ArrayList<Candy>();
            for(Candy candy : candies) {
                var playerEntity = gamePlayer.getPlayer();
                if(!playerEntity.getBoundingBox().intersect(candy)) continue;

                candy.consume(playerEntity);
                addCandy(gamePlayer, 1);
                updateLeaderboard();
                remove.add(candy);
            }
            candies.removeAll(remove);
        }
    }

    public void addCandy(GamePlayer player, int amount) {
        candiesCollected.putIfAbsent(player, amount);
        candiesCollected.computeIfPresent(player, (gamePlayer, integer) -> integer+=amount);
    }

    public void removeCandy(GamePlayer player, int amount) {
        candiesCollected.computeIfPresent(player, (gamePlayer, integer) -> integer-=amount);
    }

    public int getCandy(GamePlayer player) {
        return candiesCollected.get(player);
    }

    public Map<GamePlayer, Integer> getCandiesCollected() {
        return candiesCollected;
    }

    public Map<GamePlayer, Integer> getSortedCandiesCollected(int limit) {
        return CollectionUtils.sortByValue(candiesCollected, Comparator.reverseOrder()).entrySet()
                .stream()
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (duplicate1, duplicate2) -> duplicate1, LinkedHashMap::new));
    }

    public List<Candy> getCandies() {
        return candies;
    }

    public void displaySidebar(GamePlayer gamePlayer, Sidebar sidebar) {
        Utils.putOrReplace(sidebarDisplay, gamePlayer, sidebar);
        sidebar.addViewer(gamePlayer.getPlayer());
    }

    public void updateSidebar(GamePlayer gamePlayer, String id, Component component) {
        var sidebar = sidebarDisplay.get(gamePlayer);
        if(sidebar == null) return;

        sidebar.updateLineContent(id, component);
    }

    public void updateSidebar(String id, Function<GamePlayer, Component> function) {
        sidebarDisplay.forEach((gamePlayer, sidebar) -> sidebar.updateLineContent(id, function.apply(gamePlayer)));
    }

    public void updateSidebar(String id, Component component) {
        sidebarDisplay.forEach((gamePlayer, sidebar) -> sidebar.updateLineContent(id, component));
    }

    public void updateLeaderboard(GamePlayer gamePlayer) {
        updateSidebar("leaderboard_self", getLeaderboardFormat(gamePlayer, candiesCollected.get(gamePlayer)));
        var sorted = getSortedCandiesCollected(3);
        int index = 0;
        for(var entry : sorted.entrySet()) {
            index++;
            updateSidebar("leaderboard_" + index, getLeaderboardFormat(entry.getKey(), entry.getValue()));
        }
    }

    public void updateLeaderboard() {
        sidebarDisplay.forEach(((gamePlayer, sidebar) -> updateLeaderboard(gamePlayer)));
    }

    private Sidebar createLobbySidebar(GamePlayer viewer, String prefixColor, String valueColor) {
        var builder = createSidebarBuilder();
        builder.addLineAtStart(new SidebarBuilder.Line("game_state", MiniMessage.get().parse(prefixColor + "Waiting...")));
        builder.emptyLineAtStart();
        builder.addLineAtStart(new SidebarBuilder.Line("players", MiniMessage.get().parse(prefixColor + "Players: " + valueColor + getPlayers().size())));
        return builder.build();
    }

    private Sidebar createGameSidebar(GamePlayer viewer, String prefixColor, String valueColor) {
        var builder = createSidebarBuilder();

        createLeaderboard(viewer, builder);
        builder.emptyLineAtStart();
        builder.addLineAtStart(new SidebarBuilder.Line("distance", MiniMessage.get().parse(prefixColor + "Distance from clown: " + valueColor + getDistanceFromClown(viewer) + "m")));
        return builder.build();
    }

    private SidebarBuilder createSidebarBuilder() {
        return new SidebarBuilder(MiniMessage.get().parse("<gradient:#ff6600:#ff5050><bold>CLOWN CHASE")); // idk a name
    }

    private void createLeaderboard(GamePlayer viewer, SidebarBuilder builder) {
        var leaderboard = getSortedCandiesCollected(3);

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
        return MiniMessage.get().parse("<aqua>" + plainText.serialize(gamePlayer.getPlayer().getName()) + "<reset>: <green>" + candies + "♧");
    }

    private Pos findValidSpawn() {
        if(boundaries == null) throw new NullPointerException("Boundaries cannot be null");

        var random = ThreadLocalRandom.current();
        var randomPos = findRandomPosition(random, boundaries);
        while (!checkSurroundingArea(randomPos, 2, 2, 2)) {
            randomPos = findRandomPosition(random, boundaries);
        }

        return randomPos.sub(0,1,0);
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
        for(double x = -xArea; x < xArea; x++) {
            if(x == -xArea || x == xArea - 1) {
                for (double z = -zArea + 1; z < zArea - 1; z++) {
                    var validPos = pos.add(x, 0, z);
                    if(instance.getBlock(validPos).isAir()) return validPos;
                }
            }

            var validPos = pos.add(x, 0, 0);
            if(instance.getBlock(validPos).isAir()) return validPos;

            var validPos2 = pos.add(x, 0, zArea);
            if(instance.getBlock(validPos2).isAir()) return validPos2;
        }

//        for(double x = 0; x < xArea; x++) {
//            for(double z = 0; z < zArea; z++) {
//                for(double y = 0; y < yArea; y++) {
//                    if(!instance.getBlock(pos.add(x,y,z)).isAir()) continue;
//                    return pos.add(x, 0, z);
//                }
//            }
//        }
        return null;
    }

    private Pos findRandomPosition(ThreadLocalRandom random, Boundaries boundaries) {
        return new Pos(
                random.nextDouble(boundaries.getMinX(), boundaries.getMaxX()),
                boundaries.getMinY(),
                random.nextDouble(boundaries.getMinZ(), boundaries.getMaxZ())
        );
    }

    private void spawnClown(GamePlayer gamePlayer, Pos position) {
        var randomPos = findValidPositionInArea(position, 2, 2, 2);
        if(randomPos == null) throw new NullPointerException("Can't find valid position");

        var clown = new KillerClown(new FakePlayerOption(), player -> player.setInstance(instance, randomPos));
        var player = gamePlayer.getPlayer();
        clown.setTarget(player);
        Utils.putOrReplace(clowns, gamePlayer, clown);
    }

    private int getDistanceFromClown(GamePlayer gamePlayer) {
        var distance = 0;

        var clown = clowns.get(gamePlayer);
        if(clown != null) {
            distance = (int) clown.getDistance(gamePlayer.getPlayer());
        }

        return distance;
    }

    private void generateMaze(Pos pos, MazeGenerationStrategy strategy, MazeTheme<?,?> theme) {
        var mazeBoard = new MazeBoard(16, 16);
        strategy.execute(mazeBoard);

        this.boundaries = MazeBuilder.constructMaze(mazeBoard, theme, 6, instance, pos);
    }

    @Override
    public void endGame(Ending ending) {
        toggleClownAttacks(false);
        getPlayers().forEach(player -> player.hideBossBar(bossBar));
        for(var entry : clowns.entrySet()) {
            entry.getValue().remove();
            var gamePlayer = entry.getKey();
            var player = gamePlayer.getPlayer();
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);

            clowns.remove(gamePlayer);
        }
    }

    @Override
    public void join(GamePlayer gamePlayer) {
        super.join(gamePlayer);
        displaySidebar(gamePlayer, createLobbySidebar(gamePlayer, PREFIX_COLOR, VALUE_COLOR));
        updateSidebar("players", MiniMessage.get().parse(PREFIX_COLOR + "Players: " + VALUE_COLOR + getPlayers().size()));
    }

    @Override
    public void leave(GamePlayer gamePlayer) {
        super.leave(gamePlayer);

        sidebarDisplay.get(gamePlayer).removeViewer(gamePlayer.getPlayer());
        sidebarDisplay.remove(gamePlayer);
    }
}
