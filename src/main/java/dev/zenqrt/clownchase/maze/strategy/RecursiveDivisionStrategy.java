package dev.zenqrt.clownchase.maze.strategy;

import dev.zenqrt.clownchase.maze.MazeBoard;
import dev.zenqrt.clownchase.maze.theme.wall.WallDirection;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Maze_generation_algorithm#:~:text=the%20current%20cell.-,Recursive%20division%20method,-%5Bedit%5D">Recursive Division Method</a>
 */
public final class RecursiveDivisionStrategy implements MazeGenerationStrategy {

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;

    private final ThreadLocalRandom random;

    public RecursiveDivisionStrategy() {
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public void execute(MazeBoard board) {
        createLine(board, 0, 0, board.getDimensionX(), board.getDimensionY(), chooseOrientation(board.getDimensionX(), board.getDimensionY()));
    }

    private void createLine(MazeBoard board, int x, int y, int width, int height, int orientation) {
        if(width < 2 || height < 2) return;

        boolean horizontal = orientation == HORIZONTAL;

        int widthMath = width - 2;
        int heightMath = height - 2;
        // Chooses random point
        int wx = x + (horizontal ? 0 : widthMath > 0 ? random.nextInt(widthMath) : 0);
        int wy = y + (horizontal && heightMath > 0 ? random.nextInt(heightMath) : 0);

        // Random point for passage
        int px = wx + (horizontal ? random.nextInt(width) : 0);
        int py = wy + (horizontal ? 0 : random.nextInt(height));

        // Delta movement
        int dx = horizontal ? 1 : 0;
        int dy = horizontal ? 0 : 1;

        int length = horizontal ? width : height;

        int dir = horizontal ? WallDirection.SOUTH : WallDirection.EAST;

        for(int i = 0; i < length; i++) {
            // If the current position is at a passage way point
            if(wx != px || wy != py) {
                board.setBlock(wx, wy, dir);
            }

            // Move position
            wx += dx;
            wy += dy;
        }

        int nx = x;
        int ny = y;
        int w = horizontal ? width : wx-x+1;
        int h = horizontal ? wy-y+1 : height;
        createLine(board, nx, ny, w, h, chooseOrientation(w, h));

        nx = horizontal ? x : wx+1;
        ny = horizontal ? wy+1 : y;
        w = horizontal ? width : x+width-wx-1;
        h = horizontal ? y+height-wy-1 : height;
        createLine(board, nx, ny, w, h, chooseOrientation(w, h));
    }

    private int chooseOrientation(int width, int height) {
        return (width < height ? HORIZONTAL : height < width ? VERTICAL :  random.nextInt(2) + 1);
    }
}
