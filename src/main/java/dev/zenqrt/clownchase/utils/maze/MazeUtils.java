package dev.zenqrt.clownchase.utils.maze;

import dev.zenqrt.clownchase.maze.MazeBoard;
import dev.zenqrt.clownchase.maze.theme.wall.WallDirection;

public final class MazeUtils {

    private MazeUtils() {}

    public static void printMaze(MazeBoard board) {
        System.out.print(" " + "_".repeat(Math.max(0, board.getDimensionX() * 2 - 1)));
        for(int y = 0; y < board.getDimensionY(); y++) {
            System.out.println();
            System.out.print("|");

            for(int x = 0; x < board.getDimensionX(); x++) {
                var cell = board.getBlock(x, y);
                var bottom = y+1 >= board.getDimensionY();
                var south = cell == WallDirection.SOUTH || bottom;
                var south2 = x+1 < board.getDimensionX() && board.getBlock(x+1, y) == WallDirection.SOUTH || bottom;
                var east = cell == WallDirection.EAST || x+1 >= board.getDimensionX();

                System.out.print(south ? "_" : " ");
                System.out.print(east ? "|" : south && south2 ? "_" : " ");
            }
        }

        for (int y = 0; y < board.getDimensionY(); y++){
            for (int x = 0; x < board.getDimensionX(); x++) {
                System.out.print(board.getBlock(x, y));
            }

            System.out.println();
        }
    }

}
