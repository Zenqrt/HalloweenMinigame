package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.map.MapManager;
import dev.zenqrt.clownchase.maze.MazeBuilder;
import dev.zenqrt.clownchase.maze.strategy.MazeGenerationStrategy;
import io.papermc.paper.math.Position;

public final class SetupWorldGameState extends GameState {

    private final int mazeScale;
    private final MazeGenerationStrategy generationStrategy;
    private final MapManager mapManager;
    private final ClownChaseGame game;

    public SetupWorldGameState(ClownChaseGame game, MapManager mapManager, MazeGenerationStrategy generationStrategy, int mazeScale) {
        this.game = game;
        this.mapManager = mapManager;
        this.generationStrategy = generationStrategy;
        this.mazeScale = mazeScale;
    }

    @Override
    protected void onStateStart() {
        this.mapManager.createGameWorldAsync(this.game.getId(), world -> {
            this.game.getBoard().populate(generationStrategy);

            MazeBuilder.constructMaze(this.game.getBoard(), this.game.getTheme(), mazeScale, world, Position.block(0, 42, 0));

            this.game.setGameWorld(world);
            this.game.setWorldReady(true);

            this.game.nextState();
        });
    }
}
