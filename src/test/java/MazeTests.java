import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.utils.maze.MazeBuilder;

public class MazeTests {

    public static void main(String[] args) {
        var strategy = new RecursiveDivisionStrategy();
        var board = new MazeBoard(5,10);
        strategy.execute(board);
        MazeBuilder.printMaze(board);
    }

}
