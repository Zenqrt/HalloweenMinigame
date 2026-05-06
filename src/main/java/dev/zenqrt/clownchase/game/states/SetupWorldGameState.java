package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.maze.MazeBuilder;
import dev.zenqrt.clownchase.maze.strategy.MazeGenerationStrategy;
import dev.zenqrt.clownchase.utils.maze.MazeUtils;
import dev.zenqrt.clownchase.world.generator.VoidGenerator;
import io.papermc.paper.math.Position;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public final class SetupWorldGameState extends GameState {

    private final int mazeScale;
    private final MazeGenerationStrategy generationStrategy;
    private final ClownChaseGame game;

    public SetupWorldGameState(ClownChaseGame game, MazeGenerationStrategy generationStrategy, int mazeScale) {
        this.game = game;
        this.generationStrategy = generationStrategy;
        this.mazeScale = mazeScale;
    }

    @Override
    protected void onStateStart() {
        // Generate world  -----------    TODO: Make world gen async if possible
        World world = WorldCreator.name("clown-chase_" + this.game.getId())
                .generator(new VoidGenerator())
                .createWorld();

        if (world == null)
            throw new NullPointerException("world");

        world.setAutoSave(false);
        world.setGameRule(GameRules.SPAWN_MOBS, false);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.ADVANCE_WEATHER, false);
        world.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRules.FALL_DAMAGE, false);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);

        // Generate maze
        this.generationStrategy.execute(this.game.getBoard());

        MazeUtils.printMaze(this.game.getBoard());
        MazeBuilder.constructMaze(this.game.getBoard(), this.game.getTheme(), mazeScale, world, Position.block(0, 42, 0));

        this.game.setGameWorld(world);
        this.game.setWorldReady(true);
    }
}
