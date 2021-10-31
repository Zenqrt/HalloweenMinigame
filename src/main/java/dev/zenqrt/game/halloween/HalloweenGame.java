package dev.zenqrt.game.halloween;

import dev.zenqrt.entity.candy.Candy;
import dev.zenqrt.entity.candy.EffectCandy;
import dev.zenqrt.entity.candy.PowerUpCandy;
import dev.zenqrt.entity.candy.special.AbsorptionCandy;
import dev.zenqrt.entity.monster.KillerClown;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameOptions;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.GameState;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.game.halloween.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.game.halloween.maze.themes.HedgeMazeTheme;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.game.timers.CountdownTimerTask;
import dev.zenqrt.item.powerup.CandyMagnetItem;
import dev.zenqrt.scoreboard.SidebarBuilder;
import dev.zenqrt.server.MinestomServer;
import dev.zenqrt.timer.countdown.CountdownRunnable;
import dev.zenqrt.utils.Utils;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import dev.zenqrt.utils.collection.CollectionUtils;
import dev.zenqrt.utils.coordinate.PointUtils;
import dev.zenqrt.utils.entity.EntityUtils;
import dev.zenqrt.utils.maze.MazeBuilder;
import dev.zenqrt.utils.particle.ParticleEmitter;
import dev.zenqrt.world.collision.Boundaries;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
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
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HalloweenGame extends Game {

    public static final String PREFIX_COLOR = ParsedColor.of(TextColor.color(210, 77, 255).asHexString());
    public static final String VALUE_COLOR = ParsedColor.of(TextColor.color(77, 255, 136).asHexString());
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
    private final int maxCandies;
    private final PlainTextComponentSerializer plainText;
    private BossBar bossBar;
    private MazeTheme<?,?> theme;

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
        this.gameTime = 300;
        this.maxCandies = 100;
        this.plainText = PlainTextComponentSerializer.plainText();
    }

    @Override
    public void init() {
        generateMaze(new Pos(0, 42, 0), new RecursiveDivisionStrategy(), new HedgeMazeTheme(6, 4 ,5));
    }

    public void startGame() {
        state = GameState.IN_GAME;

        var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
        this.bossBar = BossBar.bossBar(MiniMessage.get().parse(color + "Time left: <aqua>" + gameTime + color + "s"), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        getPlayers().forEach(this::initPlayer);
        getPlayers().forEach(gamePlayer -> displaySidebar(gamePlayer, createGameSidebar(gamePlayer, PREFIX_COLOR, VALUE_COLOR)));

        var moveListener = createPlayerListener(EventListener.builder(PlayerMoveEvent.class).filter(event -> !event.getNewPosition().samePoint(event.getPlayer().getPosition()))
                .handler(event -> event.setCancelled(true)));
        var damageListener = createEntityListener(EntityDamageEvent.class, event -> event.setCancelled(true));

        mapTask("movement_countdown", new CountdownTimerTask(10,
                timer -> {
                    var audience = Audience.audience(getPlayers());
                    audience.sendActionBar(MiniMessage.get().parse(String.format(PREFIX_COLOR + "You can move in <yellow>%d " + PREFIX_COLOR + "seconds!", timer)));

                    if(timer <= 5) {
                        audience.showTitle(Title.title(
                                MiniMessage.get().parse("<yellow>"+ timer),
                                MiniMessage.get().parse(color + "Prepare to move!"),
                                Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500))
                        ));
                        audience.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_HAT, Sound.Source.PLAYER, 1, 1), Sound.Emitter.self());
                    }
                }, () -> {
            var audience = Audience.audience(getPlayers());
            audience.showTitle(Title.title(
                    MiniMessage.get().parse("<gradient:#ff6600:#ff5050><bold>RUN!"),
                    Component.empty(),
                    Title.Times.of(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
            ));
            audience.playSound(Sound.sound(SoundEvent.ENTITY_WITHER_SPAWN, Sound.Source.PLAYER, 1, 0.6f), Sound.Emitter.self());
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
                            var loss = getCandy(gamePlayer) / 4;
                            event.setCancelled(true);
                            respawning.add(player);
                            clown.setAttacking(false);
                            removeCandy(gamePlayer, loss);
                            Audience.audience(getPlayers()).sendMessage(MiniMessage.get().parse("<aqua>" + plainText.serialize(player.getName()) + " " + PREFIX_COLOR + "lost " + VALUE_COLOR + loss + " " + PREFIX_COLOR + "from death!"));
                            player.setHealth(player.getMaxHealth());
                            player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 1000));
                            player.showTitle(Title.title(Component.text("YOU DIED!", NamedTextColor.RED).decorate(TextDecoration.BOLD), Component.empty()));
                            player.playSound(Sound.sound(SoundEvent.ENTITY_ZOMBIE_DEATH, Sound.Source.PLAYER, 1, 0));
                            player.setGameMode(GameMode.SPECTATOR);
                            player.getInventory().clear();

                            new CountdownRunnable(5) {
                                @Override
                                public void beforeIncrement() {
                                    player.sendActionBar(MiniMessage.get().parse(String.format(PREFIX_COLOR + "Respawning in <yellow>%d " + PREFIX_COLOR + "seconds...", timer)));
                                }

                                @Override
                                public void endCountdown() {
                                    respawning.remove(player);
                                    respawnPlayer(player, findValidSpawn(2, 2, 2));
                                    clown.setAttacking(true);
                                    player.setGameMode(GameMode.SURVIVAL);
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

                    if(timer == gameTime - 3) {
                        removeListener(damageListener);
                    }

                    if(timer % (gameTime / 4) == 0 && timer != gameTime) {
                        clowns.values().forEach(clown -> clown.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(clown.getAttributeValue(Attribute.MOVEMENT_SPEED) + 0.05f));
                        audience.sendMessage(Component.text("As the night progresses, the clowns become faster...", NamedTextColor.DARK_PURPLE));
                        audience.playSound(Sound.sound(SoundEvent.BLOCK_PORTAL_TRAVEL, Sound.Source.PLAYER, 0.5f, 2), Sound.Emitter.self());
                    }

//                    if(timer % (gameTime / 3) == 0) {
//                        handleCandySpawning(ThreadLocalRandom.current(), true);
//                    }
                }

                @Override
                public void endCountdown() {
                    endGame();
                }
            }.repeat(Duration.of(1, TimeUnit.SECOND)).schedule());

            mapTask("game_tick", MinecraftServer.getSchedulerManager().buildTask(new Runnable() {
                int tick = 0;
                @Override
                public void run() {
                    activeGameTick(tick);
                    tick++;
                }
            }).repeat(Duration.of(1, TimeUnit.SERVER_TICK)).schedule());
        }).repeat(Duration.of(1, TimeUnit.SECOND)).schedule());
    }

    private void initPlayer(GamePlayer gamePlayer) {
        var player = gamePlayer.getPlayer();
        var position = findValidSpawn(2, 2, 2);
        respawnPlayer(player, position);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlyingSpeed(0);
        player.setAllowFlying(false);
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(6);
        player.setHealth(10);

        Utils.putOrReplace(candiesCollected, gamePlayer, 0);

        spawnClown(gamePlayer, position.add(0,1,0));
        player.showBossBar(bossBar);
    }

    private void respawnPlayer(Player player, Point position) {
        var pos = PointUtils.toCenter(position.add(0,1,0));
        if(player.getInstance() == null || !player.getInstance().equals(instance)) {
            player.setInstance(instance, pos);
        } else {
            player.teleport(Pos.fromPoint(pos));
        }
    }

    private void toggleClownAttacks(boolean attacking) {
        clowns.forEach((player, clown) -> clown.setAttacking(attacking));
    }

    private void activeGameTick(int tick) {
        if(ThreadLocalRandom.current().nextFloat() < 0.005) {
            var lightning = new Entity(EntityType.LIGHTNING_BOLT);
            lightning.setInstance(instance, new Pos(0, 255, 0));
            MinecraftServer.getSchedulerManager().buildTask(lightning::remove).delay(1, TimeUnit.SECOND).schedule();
//            Audience.audience(getPlayers()).playSound(Sound.sound(SoundEvent.ENTITY_LIGHTNING_BOLT_THUNDER, Sound.Source.WEATHER, 0.1f, 1), Sound.Emitter.self());
        }
        
        for(var gamePlayer : getPlayers()) {
            var playerEntity = gamePlayer.getPlayer();
            var boundingBox = playerEntity.getBoundingBox();
            var remove = new ArrayList<Candy>();

            if(tick % 20 == 0) {
                handleCandySpawning(ThreadLocalRandom.current(), false);
            }
            // TODO: Fix clown spawning too close sometimes
            for(var candy : candies) {
                if(!boundingBox.intersect(candy)) continue;

                candy.consume(playerEntity);
                addCandy(gamePlayer, EntityUtils.hasActiveEffect(playerEntity, PotionEffect.LUCK) ? 2 : 1);
                remove.add(candy);
            }
            candies.removeAll(remove);
        }
    }

    private void handleCandySpawning(ThreadLocalRandom random, boolean special) {
        var spawnPos = findValidSpawn(1, 1, 1);
        if(special) {
            var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
            var audience = Audience.audience(getPlayers());
            audience.sendMessage(MiniMessage.get().parse(color + "A special candy will spawn at <yellow>" + spawnPos.x() + ", " + spawnPos.y() + ", " + spawnPos.z() + color + " in 10 seconds!"));
            var candy = random.nextBoolean() ? new AbsorptionCandy() : new PowerUpCandy(new CandyMagnetItem());

            new CountdownRunnable(200) {
                @Override
                public void beforeIncrement() {
                    var emitter = new ParticleEmitter(Particle.END_ROD, new Vec(1, 1, 1), 0.01f, 10, true);
                    PacketUtils.sendPacket(audience, emitter.createPacket(spawnPos.add(0,1,0)));
                }

                @Override
                public void endCountdown() {
                    spawnCandy(spawnPos.add(0, 1, 0), candy);
                }
            }.repeat(Duration.of(1, TimeUnit.SERVER_TICK)).schedule();

        } else {
            if (candies.size() < maxCandies) {
                Candy candy;
                if (random.nextFloat() < 0.15f) {
                    if (random.nextBoolean()) {
                        var values = EffectCandy.Effect.values();
                        candy = new EffectCandy(values[random.nextInt(values.length)]);
                    } else {
                        candy = new PowerUpCandy(PowerUpCandy.POWER_UPS.get(random.nextInt(PowerUpCandy.POWER_UPS.size())));
                    }
                } else {
                    candy = new Candy(Candy.Color.RED);
                }
                spawnCandy(spawnPos.add(0, 1, 0), candy);
            }
        }
    }

    private void spawnCandy(Point point, Candy candy) {
        candy.setInstance(instance, PointUtils.toCenter(point));
        candies.add(candy);
    }

    // improve please
    public void addCandy(GamePlayer player, int amount) {
        candiesCollected.putIfAbsent(player, amount);
        candiesCollected.computeIfPresent(player, (gamePlayer, integer) -> integer+=amount);
        updateLeaderboard();
    }

    public void removeCandy(GamePlayer player, int amount) {
        candiesCollected.computeIfPresent(player, (gamePlayer, integer) -> integer-=amount);
        updateLeaderboard();
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

    public Map<GamePlayer, Integer> getSortedCandiesCollected() {
        return CollectionUtils.sortByValue(candiesCollected, Comparator.reverseOrder()).entrySet()
                .stream()
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
//        updateSidebar("leaderboard_self", getLeaderboardFormat(gamePlayer, candiesCollected.get(gamePlayer)));
//        var sorted = getSortedCandiesCollected(3);
//        int index = 0;
//        for(var entry : sorted.entrySet()) {
//            index++;
//            updateSidebar("leaderboard_" + index, getLeaderboardFormat(entry.getKey(), entry.getValue()));
//        }
        displaySidebar(gamePlayer, createGameSidebar(gamePlayer, PREFIX_COLOR, VALUE_COLOR));
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
        return new SidebarBuilder(MiniMessage.get().parse("<gradient:#ff6600:#ff5050><bold>HALLOWEEN EVENT")); // idk a name
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
            var candies = candiesCollected.get(viewer);
            if(candies != null) {
                builder.addLineAtStart(new SidebarBuilder.Line("leaderboard_self", getLeaderboardFormat(viewer, candies)));
            }
        }
    }

    private Component getLeaderboardFormat(GamePlayer gamePlayer, int candies) {
        return MiniMessage.get().parse("<aqua>" + plainText.serialize(gamePlayer.getPlayer().getName()) + "<reset>: <green>" + candies + "♧");
    }

    private Pos findValidSpawn(int xArea, int yArea, int zArea) {
        if(boundaries == null) throw new NullPointerException("Boundaries cannot be null");

        var random = ThreadLocalRandom.current();
        var randomPos = findRandomPosition(random, boundaries);
        while (!checkSurroundingArea(randomPos, xArea, yArea, zArea)) {
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

        var player = gamePlayer.getPlayer();
        var clown = new KillerClown(new FakePlayerOption(), player, null);
        if(clown.getInstance() != instance) {
            clown.setInstance(instance, PointUtils.toCenter(randomPos));
        } else {
            clown.teleport(PointUtils.toCenter(randomPos));
        }
        Utils.putOrReplace(clowns, gamePlayer, clown);
        clown.setTeam(PLAYER_TEAM);
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
        this.theme = theme;

        var mazeBoard = new MazeBoard(16, 16);
        strategy.execute(mazeBoard);

        for(int x = -25; x <= 25; x++) {
            for(int z = -25; z <= 25; z++) {
                var point = pos.add(x * 16, 0, z * 16);
                if(x == 25 && z == 25) {
                    instance.loadChunk(point).thenRun(() -> boundaries = MazeBuilder.constructMaze(mazeBoard, theme, 6, instance, pos));
                } else {
                    instance.loadChunk(point);
                }
            }
        }


    }

    @Override
    public void endGame() {
        toggleClownAttacks(false);
        getPlayers().forEach(player -> {
            var playerEntity = player.getPlayer();
            playerEntity.hideBossBar(bossBar);
            playerEntity.setGameMode(GameMode.ADVENTURE);
            playerEntity.getInventory().clear();
        });

        for(var entry : clowns.entrySet()) {
            entry.getValue().remove();
            var gamePlayer = entry.getKey();
            var player = gamePlayer.getPlayer();
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        }
        clowns.clear();
        super.endGame();

        var map = getSortedCandiesCollected();

        displayWinners(map);
        sendEndingTitles(new ArrayList<>(map.keySet()));

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            var game = MinestomServer.getGameManager().createHalloweenGame();
            MinestomServer.getGameManager().setMainGame(game);
            getPlayers().forEach(gamePlayer -> {
                var player = gamePlayer.getPlayer();
                player.setInstance(MinestomServer.getInstanceContainer(), new Pos(0.5, 68, 0.5));
                player.setHealth(player.getMaxHealth());
                leave(gamePlayer);
                game.join(gamePlayer);
            });
        })
                .delay(10, TimeUnit.SECOND).schedule();
    }

    private void sendEndingTitles(List<GamePlayer> sorted) {
        for(int i = 0; i < sorted.size(); i++) {
            Component title,subtitle;
            if(i == 0) {
                title = MiniMessage.get().parse("<red><bold>1st place");
                subtitle = MiniMessage.get().parse("<gray>You win!");
            } else if(i == 1) {
                title = MiniMessage.get().parse("<gold><bold>2nd place");
                subtitle = MiniMessage.get().parse("<gray>So close!");
            } else if(i == 2) {
                title = MiniMessage.get().parse("<yellow><bold>3rd place");
                subtitle = MiniMessage.get().parse("<gray>Better luck next time!");
            } else {
                title = MiniMessage.get().parse("<green><bold>" + i + "th place");
                subtitle = MiniMessage.get().parse("<gray>Yikes...");
            }
            sorted.get(i).showTitle(Title.title(title, subtitle));
        }
    }

    private void displayWinners(Map<GamePlayer, Integer> sortedMap) {
        var sorted = new ArrayList<>(sortedMap.keySet());

        var stringBuilder = new StringBuilder();

        for(int i = 0; i < 3; i++) {
            stringBuilder.append("\n");
            var prefix = i == 0 ? "<red><bold>1st place" : i == 1 ? "<gold><bold>2nd place" : "<yellow><bold>3rd place";
            var player = i >= sorted.size() ? null : sorted.get(i);
            stringBuilder.append(prefix).append(" <gray>- ").append(player == null ? "<dark_gray>None" : "<aqua>" + plainText.serialize(player.getPlayer().getName()));
        }

        Audience.audience(getPlayers()).sendMessage(MiniMessage.get().parse(stringBuilder.toString()));
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

    public MazeTheme<?, ?> getTheme() {
        return theme;
    }
}
