package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.candy.CandyType;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.entity.candy.Candy;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.item.items.DamageTrapItem;
import dev.zenqrt.clownchase.item.items.StunBallItem;
import dev.zenqrt.clownchase.sidebar.sidebars.ClownChaseSidebar;
import dev.zenqrt.clownchase.utils.attribute.GameAttributeModifiers;
import dev.zenqrt.clownchase.utils.text.Messages;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import dev.zenqrt.clownchase.utils.world.PositionUtils;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class ChaseGameState extends GameState implements Listener {

    private static final String DEATH_TITLE = "game.death.title";
    private static final String DEATH_MESSAGE = "game.death.message";
    private static final String RESPAWN_COUNTDOWN_TIMER = "game.respawn.timer";
    private static final String RESPAWN_TITLE = "game.respawn.title";
    private static final String TIME_LEFT = "game.time_left";
    private static final String CLOWN_BUFF = "game.clown_buff";
    private static final String SHIELD_BROKEN = "game.shield.broken";
    private static final Sound DEATH_SOUND = Sound.sound(Key.key("minecraft:entity.zombie.death"), Sound.Source.MASTER, 1, 0);
    private static final Sound CONSUME_SOUND = Sound.sound(Key.key("minecraft:entity.player.burp"), Sound.Source.MASTER, 1, 1.5F);
    private static final Sound CLOWN_BUFF_SOUND = Sound.sound(Key.key("minecraft:block.portal.travel"), Sound.Source.MASTER, 0.5F, 2);

    private final List<Candy> spawnedCandies = new ArrayList<>();
    private final SpawnCandyTask spawnCandyTask;

    private float clownSpeedMultiplier;

    private final List<CustomItem.Listeners> itemListeners;
    private final List<BukkitTask> tasks = new ArrayList<>();
    private final Map<UUID, ClownChaseSidebar> sidebarMap = new HashMap<>();
    private final ClownChaseGame game;

    public ChaseGameState(ClownChaseGame game) {
        this.game = game;
        this.spawnCandyTask = new SpawnCandyTask(1, 3, 100);
        this.clownSpeedMultiplier = 0;

        this.itemListeners = List.of(
                new StunBallItem.Listeners(game),
                new DamageTrapItem.Listeners(game, this)
        );
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());
        this.itemListeners.forEach(item -> Bukkit.getPluginManager().registerEvents(item, this.game.getPlugin()));

        this.game.getPlayerToClown().forEach((_, clown) -> clown.setNoAi(false));
        this.game.getPlayers().forEach((_, gamePlayer) -> {
            ClownChaseSidebar sidebar = new ClownChaseSidebar();
            sidebar.addViewer(((CraftPlayer) gamePlayer.validatePlayer()).getHandle());

            this.sidebarMap.put(gamePlayer.getUniqueId(), sidebar);
        });

        tasks.add(Bukkit.getScheduler().runTaskTimer(this.game.getPlugin(), spawnCandyTask, 0, 20));
        tasks.add(Bukkit.getScheduler().runTaskTimer(this.game.getPlugin(), new CandyCollectDetectionTask(), 0, 1));
        tasks.add(Bukkit.getScheduler().runTaskTimer(this.game.getPlugin(), new GameTimerTask(this.game.getGameSettings().gameLength()), 0, 20));
    }

    @Override
    protected void onStateEnd() {
        HandlerList.unregisterAll(this);
        this.itemListeners.forEach(CustomItem.Listeners::unregister);

        tasks.forEach(BukkitTask::cancel);
        tasks.clear();

        this.game.getPlayers().forEach((_, gamePlayer) -> {
            Player player = gamePlayer.validatePlayer();

            AttributeInstance movementSpeed = Objects.requireNonNull(player.getAttribute(Attribute.MOVEMENT_SPEED), "movementSpeed");
            AttributeInstance maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH), "maxHealth");

            movementSpeed.removeModifier(GameAttributeModifiers.SPEED_KEY);
            maxHealth.removeModifier(GameAttributeModifiers.MAX_HEALTH_KEY);

            player.setHealth(maxHealth.getValue());
        });

        this.game.getPlayerToClown().forEach((_, clown) -> clown.remove(Entity.RemovalReason.DISCARDED));
        this.game.clearClowns();

        this.spawnedCandies.forEach(candy -> candy.remove(Entity.RemovalReason.DISCARDED));
        this.spawnedCandies.clear();

        this.sidebarMap.forEach((_, sidebar) -> sidebar.removeAllViewers());
        this.sidebarMap.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || !this.game.hasPlayer(player.getUniqueId()))
            return;

        if (!(event.getDamager() instanceof Husk))
            return;

        GamePlayerData playerData = this.game.getPlayerData(player.getUniqueId());

        if (!playerData.isShielded())
            return;

        event.setDamage(0);
        this.game.breakShield(player, playerData);
        player.sendMessage(Component.translatable(SHIELD_BROKEN, NamedTextColor.RED).decorate(TextDecoration.BOLD));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!this.game.getPlayers().containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        GamePlayerData playerData = this.game.getPlayerData(player.getUniqueId());
        int candyLoss = playerData.getCandyCollected() / 4;

        playerData.setAlive(false);
        playerData.removeCandyCollected(candyLoss);

        updateCandyLeaderboard();

        player.setGameMode(GameMode.SPECTATOR);
        player.setFlySpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000, 1, true, false, false));
        player.showTitle(Title.title(
                Component.translatable(DEATH_TITLE, NamedTextColor.RED).decorate(TextDecoration.BOLD),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
        ));
        player.playSound(DEATH_SOUND, Sound.Emitter.self());

        tasks.add(new RespawnCountdownTask(5, player, this.game.getPlayerToClown().get(player.getUniqueId()))
                .runTaskTimer(this.game.getPlugin(), 0, 20));

        this.game.audience().sendMessage(
                Component.text("☠ ", NamedTextColor.RED)
                        .append(Component.translatable(DEATH_MESSAGE, NamedTextColor.GRAY,
                                Messages.username(player), Messages.candyText(candyLoss)))
        );
    }

    private void respawnPlayer(GamePlayerData playerData, Player player, Clown assignedClown) {
        BlockPosition spawn;

        do {
            spawn = this.game.findAvailableSpawn(1, 1);
        } while (PositionUtils.distance(assignedClown.getBukkitEntity().getLocation(), spawn) < 10);

        player.teleport(spawn.toLocation(this.game.getGameWorld()));
        player.setHealth(this.game.getGameSettings().maxHealth());
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        playerData.setAlive(true);
    }

    public void updateCandyLeaderboard() {
        List<ClownChaseGame.LeaderboardEntry> leaderboard = ChaseGameState.this.game.getCandyLeaderboard(3);
        Pair<String, Integer> firstPlaceDisplay = getPlaceDisplay(leaderboard, 1);
        Pair<String, Integer> secondPlaceDisplay = getPlaceDisplay(leaderboard, 2);
        Pair<String, Integer> thirdPlaceDisplay = getPlaceDisplay(leaderboard, 3);

        for (UUID uuid : ChaseGameState.this.game.getPlayers().keySet()) {
            ClownChaseSidebar sidebar = ChaseGameState.this.sidebarMap.get(uuid);

            sidebar.setFirstPlaceScore(firstPlaceDisplay.first(), firstPlaceDisplay.second());
            sidebar.setSecondPlaceScore(secondPlaceDisplay.first(), secondPlaceDisplay.second());
            sidebar.setThirdPlaceScore(thirdPlaceDisplay.first(), thirdPlaceDisplay.second());
        }
    }

    private Pair<String, Integer> getPlaceDisplay(List<ClownChaseGame.LeaderboardEntry> leaderboard, int place) {
        int index = place - 1;

        if (index >= leaderboard.size())
            return Pair.of("...", 0);

        ClownChaseGame.LeaderboardEntry entry = leaderboard.get(index);

        return Pair.of(entry.gamePlayer().validatePlayer().getName(), entry.playerData().getCandyCollected());
    }

    private class GameTimerTask implements Runnable {

        private int currentTime;
        private final int gameTime;
        private final BossBar bossBar;

        GameTimerTask(int gameTime) {
            this.gameTime = gameTime;
            this.currentTime = gameTime;

            this.bossBar = BossBar.bossBar(
                    bossBarTitle(currentTime),
                    1F,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.NOTCHED_6
            );

            this.bossBar.addViewer(ChaseGameState.this.game.audience());
        }

        @Override
        public void run() {
            Audience audience = ChaseGameState.this.game.audience();

            if (--this.currentTime <= 0) {
                this.bossBar.removeViewer(audience);
                ChaseGameState.this.game.nextState();
                return;
            }

            updateTimerBossBar();
            updatePlayerDisplays();

            if (this.currentTime % (this.gameTime / 3) == 0)
                upgradeClownSpeed(audience);
        }

        private void updateTimerBossBar() {
            this.bossBar.name(bossBarTitle(currentTime));
            this.bossBar.progress((float) this.currentTime / this.gameTime);
        }

        private void updatePlayerDisplays() {
            for (ClownChasePlayer gamePlayer : ChaseGameState.this.game.getPlayers().values()) {
                Player player = gamePlayer.validatePlayer();

                // Clown display
                Clown clown = ChaseGameState.this.game.getPlayerToClown().get(player.getUniqueId());

                ClownChaseSidebar sidebar = ChaseGameState.this.sidebarMap.get(player.getUniqueId());
                sidebar.setClownDistance((int) PositionUtils.distance(player.getLocation(), Position.fine(clown.getX(), clown.getY(), clown.getZ())));

                // Shield display
                GamePlayerData playerData = ChaseGameState.this.game.getPlayerData(player.getUniqueId());

                if (playerData.isShielded()) {
                    player.sendActionBar(
                            Component.text("[", NamedTextColor.DARK_GRAY)
                                    .append(Component.text(" \uD83D\uDEE1 ", NamedTextColor.LIGHT_PURPLE))
                                    .append(Component.text("]"))
                    );
                } else {
                    player.sendActionBar(
                            Component.text("[   ]", NamedTextColor.DARK_GRAY)
                    );
                }
            }
        }

        private void upgradeClownSpeed(Audience audience) {
            clownSpeedMultiplier += 0.12F;

            audience.sendMessage(Component.translatable(CLOWN_BUFF, NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC));
            audience.playSound(CLOWN_BUFF_SOUND, Sound.Emitter.self());

            for (Clown clown : ChaseGameState.this.game.getPlayerToClown().values()) {
                clown.setSpeedModifier(clownSpeedMultiplier);
            }
        }

        private Component bossBarTitle(int timeSeconds) {
            int seconds = timeSeconds % 60;
            int minutes = timeSeconds / 60;

            Component timeLeft = Component.text(minutes + ":" + String.format("%02d", seconds), NamedTextColor.AQUA);

            return Component.translatable(TIME_LEFT, NamedTextColor.YELLOW, timeLeft);
        }
    }

    private class CandyCollectDetectionTask implements Runnable {

        @Override
        public void run() {
            for (ClownChasePlayer gamePlayer : ChaseGameState.this.game.getPlayers().values()) {
                GamePlayerData playerData = ChaseGameState.this.game.getPlayerData(gamePlayer.getUniqueId());

                if (!playerData.isAlive())
                    continue;

                Player player = gamePlayer.validatePlayer();

                player.getWorld()
                        .getNearbyEntities(player.getLocation(),
                                player.getBoundingBox().getWidthX(),
                                player.getBoundingBox().getHeight(),
                                player.getBoundingBox().getWidthZ(),
                                entity -> ((CraftEntity) entity).getHandle() instanceof Candy)
                        .stream()
                        .map(entity -> (Candy) ((CraftEntity) entity).getHandle())
                        .forEach(candy -> {
                            consumeCandy(candy, player, playerData);
                            updateCandyLeaderboard();
                        });
            }
        }

        private void consumeCandy(Candy candy, Player player, GamePlayerData playerData) {
            candy.onConsume(ChaseGameState.this.game, playerData, player);
            candy.remove(Entity.RemovalReason.DISCARDED);

            playerData.addCandyCollected(1);

            player.setLevel(playerData.getCandyCollected());
            player.playSound(CONSUME_SOUND, Sound.Emitter.self());

            ChaseGameState.this.spawnedCandies.remove(candy);
        }

    }

    private class SpawnCandyTask implements Runnable {

        private int timeSinceSpawn;
        private final int maxCandy;
        private final int candiesPerSpawn;
        private final int interval;

        SpawnCandyTask(int intervalSeconds, int candiesPerSpawn, int maxCandy) {
            this.interval = intervalSeconds;
            this.candiesPerSpawn = candiesPerSpawn;
            this.maxCandy = maxCandy;
        }

        @Override
        public void run() {
            ++this.timeSinceSpawn;

            if (ChaseGameState.this.spawnedCandies.size() >= maxCandy)
                return;

            if (this.timeSinceSpawn < this.interval)
                return;

            ServerLevel level = ((CraftWorld) ChaseGameState.this.game.getGameWorld()).getHandle();

            for (int i = 0; i < candiesPerSpawn; i++) {
                Candy candy = pickRandomCandyType(ThreadLocalRandom.current()).create(level);

                spawnCandy(level, candy);
            }
        }

        public CandyType pickRandomCandyType(Random random) {
            int totalWeight = Arrays.stream(CandyType.values())
                    .mapToInt(CandyType::weight)
                    .sum();

            int roll = random.nextInt(totalWeight);

            for (CandyType type : CandyType.values()) {
                roll -= type.weight();

                if (roll < 0)
                    return type;
            }

            throw new IllegalStateException("Failed to pick random candy type");
        }

        private void spawnCandy(ServerLevel level, Candy candy) {
            Position spawn = ChaseGameState.this.game.findAvailableSpawn(0, 0)
                    .offset(0.5, 1.5, 0.5);

            candy.setPos(spawn.x(), spawn.y(), spawn.z());
            level.addFreshEntity(candy);

            ChaseGameState.this.spawnedCandies.add(candy);
        }
    }

    private class RespawnCountdownTask extends BukkitRunnable {

        private int currentTime;
        private final Clown clown;
        private final Player player;

        RespawnCountdownTask(int time, Player player, Clown clown) {
            this.player = player;
            this.clown = clown;
            this.currentTime = time;
        }

        @Override
        public void run() {
            if (--this.currentTime <= 0) {
                GamePlayerData playerData = ChaseGameState.this.game.getPlayerData(this.player.getUniqueId());

                this.player.showTitle(Title.title(
                        Component.translatable(RESPAWN_TITLE, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
                ));
                respawnPlayer(playerData, this.player, this.clown);

                this.cancel();
                return;
            }

            this.player.setFlySpeed(0);
            this.player.sendActionBar(
                    Component.translatable(RESPAWN_COUNTDOWN_TIMER, TextColorPresets.TEXT, Component.text(currentTime, TextColorPresets.NUMBER))
            );
        }
    }

}
