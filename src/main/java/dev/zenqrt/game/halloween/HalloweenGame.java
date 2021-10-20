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
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

public class HalloweenGame extends Game {

    private final List<Candy> candies;
    private final Instance instance;

    private int gameTime;

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
    }

    private void spawnClowns() {

    }

    private void generateMaze(Pos pos, MazeGenerationStrategy strategy, MazeTheme<?,?> theme) {
        var mazeBoard = new MazeBoard(16, 16);
        strategy.execute(mazeBoard);

        MazeBuilder.constructMaze(mazeBoard, theme, 6, instance, pos);
    }

    @Override
    public void endGame(Ending ending) {

    }
}
