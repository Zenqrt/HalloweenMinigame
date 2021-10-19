package dev.zenqrt.game.halloween.maze.strategy;

import com.extollit.linalg.immutable.Vec2d;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.wall.WallDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Maze_generation_algorithm#:~:text=the%20current%20cell.-,Recursive%20division%20method,-%5Bedit%5D">Recursive Division Method</a>
 */
public class RecursiveDivisionStrategy implements MazeGenerationStrategy {

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;

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
        createLine(board, 0, 0, board.getDimensionX(), board.getDimensionY(), chooseOrientation(board.getDimensionX(), board.getDimensionY()));
    }

    public void displayMaze(MazeBoard board) {
        var grid = board.getBoard();

        for(int y = 0; y < grid.length; y++) {
            var row = grid[y];
            System.out.println("|");
            System.out.println(" " + "_".repeat(Math.max(0, grid[0].length * 2 - 1)));

            for(int x = 0; x < row.length; x++) {
                var cell = grid[y][x];
                var bottom = y+1 >= grid.length;
                var south = cell == WallDirection.SOUTH || bottom;
                var south2 = x+1 < grid[y].length && grid[y][x+1] == WallDirection.SOUTH || bottom;
                var east = cell == WallDirection.EAST || x+1 >= grid[y].length;

                System.out.println(south ? "_" : " ");
                System.out.println(east ? "|" : south && south2 ? "_" : " ");
            }
        }
    }

    private void createLine(MazeBoard board, int x, int y, int width, int height, int orientation) {
        if(width < 2 || height < 2) return;

        var horizontal = orientation == HORIZONTAL;

        var widthMath = width - 2;
        var heightMath = height - 2;
        var wx = x + (horizontal ? 0 : widthMath > 0 ? random.nextInt(widthMath) : 0);
        var wy = y + (horizontal && heightMath > 0 ? random.nextInt(heightMath) : 0);

        var px = wx + (horizontal ? random.nextInt(width) : 0);
        var py = wy + (horizontal ? 0 : random.nextInt(height));

        var dx = horizontal ? 1 : 0;
        var dy = horizontal ? 0 : 1;

        var length = horizontal ? width : height;

        var dir = horizontal ? WallDirection.SOUTH : WallDirection.EAST;

        for(int i = 0; i < length; i++) {
            System.out.println("i = " + i);
            System.out.println("wx = " + wx);
            System.out.println("wy = " + wy);
            System.out.println("dx = " + dx);
            System.out.println("dy = " + dy);
            if(wx != px || wy != py) {
                System.out.println("SET BLOCK");
                board.setBlock(wx, wy, dir);
            }
            wx += dx;
            wy += dy;
        }

        var nx = x;
        var ny = y;
        var w = horizontal ? width : wx-x+1;
        var h = horizontal ? wy-y+1 : height;
        createLine(board, nx, ny, w, h, chooseOrientation(w, h));

        nx = horizontal ? x : wx+1;
        ny = horizontal ? wy+1 : y;
        w = horizontal ? width : x+width-wx-1;
        h = horizontal ? y+height-1 : height;
        createLine(board, nx, ny, w, h, chooseOrientation(w, h));
    }

    private int chooseOrientation(int width, int height) {
        return (width < height ? HORIZONTAL : height < width ? VERTICAL :  random.nextInt(2) + 1);
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
