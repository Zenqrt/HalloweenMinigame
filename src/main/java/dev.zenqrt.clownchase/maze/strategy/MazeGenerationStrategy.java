package dev.zenqrt.clownchase.maze.strategy;

import dev.zenqrt.clownchase.maze.MazeBoard;

public interface MazeGenerationStrategy {
    void execute(MazeBoard board);
}
