package dev.zenqrt.game.halloween.maze.strategy;

import com.extollit.linalg.immutable.Vec2d;
import dev.zenqrt.game.halloween.maze.MazeBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Maze_generation_algorithm#:~:text=the%20current%20cell.-,Recursive%20division%20method,-%5Bedit%5D">Recursive Division Method</a>
 */
public class RecursiveDivisionStrategy implements MazeGenerationStrategy {

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;
    private static final int S = 1;
    private static final int E = 2;

    private final ThreadLocalRandom random;

    public RecursiveDivisionStrategy() {
        this.random = ThreadLocalRandom.current();
    }

    /*
    * Method:
    * Create 2 random lines
    * Divide lines into separate walls at intersection; should result in 4 walls
    * Choose 3 random walls and create a hole at a random point of the wall
    * Repeat
     */
    @Override
    public void execute(MazeBoard board) {
        while(true) {
            if(!hasEmptyCells(board)) break;

            var randomPoint = random.nextInt(board.getDimensionY());
            if(validatePointX(board, randomPoint)) continue;

            var line = createVerticalLine(board, randomPoint);
            createPassage(board, line);
        }
    }

    private void createLine(MazeBoard board, int x, int y, int width, int height, int orientation) {
        if(width < 2 || height < 2) return;

        var horizontal = orientation == HORIZONTAL;

        var wx = x + (horizontal ? 0 : random.nextInt(width - 2));
        var wy = y + (horizontal ? random.nextInt(height - 2) : 0);

        var px = wx + (horizontal ? random.nextInt(width) : 0);
        var py = wy + (horizontal ? 0 : random.nextInt(height));

        var dx = horizontal ? 1 : 0;
        var dy = horizontal ? 0 : 1;

        var length = horizontal ? width : height;

        var dir = horizontal ? S : E;

        for(int i = 0; i < length; i++) {
            board.
        }
    }

    private List<Vec2d> createVerticalLine(MazeBoard board, int point) {
        var list = new ArrayList<Vec2d>();
        for(int i = 0; i < board.getDimensionY(); i++) {
            board.setBlock(point, i, true);
            list.add(new Vec2d(point, i));
        }
        return list;
    }

    private List<Vec2d> createHorizontalLine(MazeBoard board, int point) {
        var list = new ArrayList<Vec2d>();
        for(int i = 0; i < board.getDimensionX(); i++) {
            board.setBlock(i, point, true);
            list.add(new Vec2d(i, point));
        }
        return list;
    }

    private void createPassage(MazeBoard board, List<Vec2d> list) {
        var v = list.get(random.nextInt(list.size()));
        board.setBlock((int) v.x, (int) v.y, false);
        list.remove(v);
    }

    private boolean validatePointX(MazeBoard board, int point) {
        return !(board.hasBlock(point, 0) || board.hasBlock(point + 1, 0) || board.hasBlock(point - 1, 0));
    }

    private boolean hasEmptyCells(MazeBoard board) {
        for(int x = 0; x < board.getDimensionX(); x++) {
            for(int y = 0; y < board.getDimensionY(); y++) {
                if(!board.hasBlock(x, y)) return true;
            }
        }
        return false;
    }
}
