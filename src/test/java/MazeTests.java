import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.RecursiveDivisionStrategy;

public class MazeTests {

    public static void main(String[] args) {
        var strategy = new RecursiveDivisionStrategy();
        var board = new MazeBoard(10, 10);
        strategy.execute(board);
        strategy.displayMaze(board);
    }

}
