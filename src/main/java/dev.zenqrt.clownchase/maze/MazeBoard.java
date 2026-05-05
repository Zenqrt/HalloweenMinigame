package dev.zenqrt.clownchase.maze;

public final class MazeBoard {

    private final int[][] board;

    public MazeBoard(int dimX, int dimY) {
        this.board = new int[dimY][dimX];
    }

    public void setBlock(int x, int y, int wallDirection) {
        board[y][x] = wallDirection;
    }

    public boolean hasBlock(int x, int y) {
        return board[y][x] > 0;
    }

    public boolean isOpenCell(int x, int y) {
        return !(hasBlock(x, y) ||
                hasBlock(x + 1, y) ||
                hasBlock(x - 1, y) ||
                hasBlock(x, y + 1) ||
                hasBlock(x, y - 1)
        );
    }

    public int getBlock(int x, int y) {
        return board[y][x];
    }

    public int getDimensionX() {
        return board[0].length;
    }

    public int getDimensionY() {
        return board.length;
    }
}
