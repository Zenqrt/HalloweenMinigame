package dev.zenqrt.game.halloween;

import dev.zenqrt.entity.Candy;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameOptions;
import dev.zenqrt.game.GameState;
import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.game.halloween.maze.themes.wall.MazeWallDecoration;

import java.util.ArrayList;
import java.util.List;

public class HalloweenGame extends Game {

    private final List<Candy> candies;

    private int gameTime;

    public HalloweenGame(int id) {
        super(id, new GameOptions.Builder()
                .minPlayers(2)
                .maxPlayers(8)
                .canJoinInGame(false)
                .build()
        );
        this.candies = new ArrayList<>();
    }

    @Override
    public void startGame() {
        state = GameState.IN_GAME;
    }

    private void spawnClowns() {

    }

    private void generateMaze(MazeGenerationStrategy strategy, MazeWallDecoration decoration) {
        var mazeBoard = new MazeBoard(64, 64);
        strategy.execute(mazeBoard);
    }

    @Override
    public void endGame(Ending ending) {

    }
}
