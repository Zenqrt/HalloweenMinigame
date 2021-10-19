package dev.zenqrt.game.halloween.maze;

import dev.zenqrt.game.halloween.maze.themes.wall.WallDirection;

public class MazeBoard {

    protected final int[][] board;

    public MazeBoard(int dimensionX, int dimensionY) {
        this(new int[dimensionY][dimensionX]);
    }

    public MazeBoard(int[][] board) {
        this.board = board;
    }

    public void setBlock(int coordinateX, int coordinateY, int wallDirection) {
        board[coordinateY][coordinateX] = wallDirection;
    }

    public boolean hasBlock(int coordinateX, int coordinateY) {
        return board[coordinateY][coordinateX] > 0;
    }

    public boolean isOpenCell(int coordinateX, int coordinateY) {
        return !(hasBlock(coordinateX, coordinateY) ||
                hasBlock(coordinateX + 1, coordinateY) ||
                hasBlock(coordinateX - 1, coordinateY) ||
                hasBlock(coordinateX, coordinateY + 1) ||
                hasBlock(coordinateX, coordinateY - 1)
        );
    }

    public int[][] getBoard() {
        return board;
    }

    public int getDimensionX() {
        return board[0].length;
    }

    public int getDimensionY() {
        return board.length;
    }

}
