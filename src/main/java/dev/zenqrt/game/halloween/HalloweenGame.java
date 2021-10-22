package dev.zenqrt.game.halloween;

import dev.zenqrt.entity.Candy;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameOptions;
import dev.zenqrt.game.GameState;
import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.game.halloween.maze.themes.ground.MazeGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.MazeWallDecoration;
import dev.zenqrt.utils.maze.MazeBuilder;
import dev.zenqrt.world.collision.Boundaries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.time.TimeUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HalloweenGame extends Game {

    private final List<Candy> candies;
    private final Instance instance;

    private int gameTime;
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
    }

    @Override
    public void startGame() {
        state = GameState.IN_GAME;
        players.forEach(player -> player.getPlayer().setInstance(instance, findValidSpawn()));

        var moveListener = createListener(PlayerMoveEvent.class, event -> event.setCancelled(true));

        MinecraftServer.getSchedulerManager().buildTask(new Runnable() {
            int timer = 10;
            boolean run = false;
            @Override
            public void run() {
                if(timer <= 0 && !run) {
                    removeListener(moveListener);
                    run = true;
                    return;
                }

                if(timer % 10 == 0 || timer <= 5) {
                    players.forEach(player -> player.getPlayer().showTitle(Title.title(Component.text("The game starts in", NamedTextColor.YELLOW), Component.text(timer + " seconds", NamedTextColor.RED))));
                }
                timer--;
            }
        }).repeat(1, TimeUnit.SECOND);
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
