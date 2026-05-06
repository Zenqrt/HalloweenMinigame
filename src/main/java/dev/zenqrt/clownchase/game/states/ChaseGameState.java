package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.entity.Candy;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.entity.RegularCandy;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.sidebar.sidebars.ClownChaseSidebar;
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
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;

public final class ChaseGameState extends GameState implements Listener {

    private static final String DEATH_TITLE = "game.death.title";
    private static final String RESPAWN_COUNTDOWN_TIMER = "game.respawn.timer";
    private static final String RESPAWN_TITLE = "game.respawn.title";
    private static final String TIME_LEFT = "game.time_left";
    private static final String CLOWN_BUFF = "game.clown_buff";
    private static final Sound DEATH_SOUND = Sound.sound(Key.key("minecraft:entity.zombie.death"), Sound.Source.MASTER, 1, 0);
    private static final Sound CONSUME_SOUND = Sound.sound(Key.key("minecraft:entity.player.burp"), Sound.Source.MASTER, 1, 1.5F);
    private static final Sound CLOWN_BUFF_SOUND = Sound.sound(Key.key("minecraft:block.portal.travel"), Sound.Source.MASTER, 0.5F, 2);

    private final List<Candy> spawnedCandies = new ArrayList<>(); // TODO: Change this to an int counter if saved candies aren't going to be used
    private final SpawnCandyTask spawnCandyTask;

    private float clownSpeedMultiplier;

    private final List<BukkitTask> tasks = new ArrayList<>();
    private final Map<UUID, ClownChaseSidebar> sidebarMap = new HashMap<>();
    private final ClownChaseGame game;

    public ChaseGameState(ClownChaseGame game) {
        this.game = game;
        this.spawnCandyTask = new SpawnCandyTask(1, 3, 100);
        this.clownSpeedMultiplier = 0;
    }

    @Override
    protected void onStateStart() {
        Bukkit.getPluginManager().registerEvents(this, this.game.getPlugin());
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

        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!this.game.getPlayers().containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        GamePlayerData playerData = this.game.getPlayerData(player.getUniqueId());
        playerData.setAlive(false);

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
    }

    private void respawnPlayer(Player player, Clown assignedClown) {
        BlockPosition spawn;

        do {
            spawn = this.game.findAvailableSpawn(1, 1);
        } while (PositionUtils.distance(assignedClown.getBukkitEntity().getLocation(), spawn) < 10);

        player.teleport(spawn.toLocation(this.game.getGameWorld()));
        player.setHealth(this.game.getGameSettings().maxHealth());
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.setGameMode(GameMode.ADVENTURE);
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

            this.bossBar.name(bossBarTitle(currentTime));
            this.bossBar.progress((float) this.currentTime / this.gameTime);

            for (ClownChasePlayer gamePlayer : ChaseGameState.this.game.getPlayers().values()) {
                Player player = gamePlayer.validatePlayer();
                Clown clown = ChaseGameState.this.game.getPlayerToClown().get(player.getUniqueId());

                ClownChaseSidebar sidebar = ChaseGameState.this.sidebarMap.get(player.getUniqueId());
                sidebar.setClownDistance((int) PositionUtils.distance(player.getLocation(), Position.fine(clown.getX(), clown.getY(), clown.getZ())));
            }

            if (this.currentTime % (this.gameTime / 3) == 0) {
                clownSpeedMultiplier += 0.12F;

                audience.sendMessage(Component.translatable(CLOWN_BUFF, NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC));
                audience.playSound(CLOWN_BUFF_SOUND, Sound.Emitter.self());

                for (Clown clown : ChaseGameState.this.game.getPlayerToClown().values()) {
                    clown.setSpeedModifier(clownSpeedMultiplier);
                }
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
            candy.remove(Entity.RemovalReason.DISCARDED);
            playerData.addCandyCollected(1);

            player.setLevel(playerData.getCandyCollected());
            player.playSound(CONSUME_SOUND, Sound.Emitter.self());

            ChaseGameState.this.spawnedCandies.remove(candy);
        }

        private void updateCandyLeaderboard() {
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
                spawnCandy(level, new RegularCandy(level));
            }
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
                this.player.showTitle(Title.title(
                        Component.translatable(RESPAWN_TITLE, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
                ));
                respawnPlayer(this.player, this.clown);

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
