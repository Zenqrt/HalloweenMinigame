package dev.zenqrt.game.halloween.maze;

public class MazeBoard {

    protected final boolean[][] board;

    public MazeBoard(int dimensionX, int dimensionY) {
        this(new boolean[dimensionX][dimensionY]);
    }

    public MazeBoard(boolean[][] board) {
        this.board = board;
    }

    public void setBlock(int coordinateX, int coordinateY, boolean block) {
        board[coordinateY][coordinateX] = block;
    }

    public boolean hasBlock(int coordinateX, int coordinateY) {
        return board[coordinateY][coordinateX];
    }

    public boolean isOpenCell(int coordinateX, int coordinateY) {
        return !(hasBlock(coordinateX, coordinateY) ||
                hasBlock(coordinateX + 1, coordinateY) ||
                hasBlock(coordinateX - 1, coordinateY) ||
                hasBlock(coordinateX, coordinateY + 1) ||
                hasBlock(coordinateX, coordinateY - 1)
        );
    }

    public int getDimensionX() {
        return board[0].length;
    }

    public int getDimensionY() {
        return board.length;
    }

}
