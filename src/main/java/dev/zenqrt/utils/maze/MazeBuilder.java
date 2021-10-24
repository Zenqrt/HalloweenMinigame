package dev.zenqrt.utils.maze;

import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.game.halloween.maze.themes.ground.MazeGroundDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.MazeWallDecoration;
import dev.zenqrt.game.halloween.maze.themes.wall.WallDirection;
import dev.zenqrt.world.collision.Boundaries;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;

public class MazeBuilder {

    public static void printMaze(MazeBoard board) {
        var grid = board.getBoard();

        System.out.print(" " + "_".repeat(Math.max(0, grid[0].length * 2 - 1)));
        for(int y = 0; y < grid.length; y++) {
            var row = grid[y];
            System.out.println();
            System.out.print("|");

            for(int x = 0; x < row.length; x++) {
                var cell = grid[y][x];
                var bottom = y+1 >= grid.length;
                var south = cell == WallDirection.SOUTH || bottom;
                var south2 = x+1 < grid[y].length && grid[y][x+1] == WallDirection.SOUTH || bottom;
                var east = cell == WallDirection.EAST || x+1 >= grid[y].length;

                System.out.print(south ? "_" : " ");
                System.out.print(east ? "|" : south && south2 ? "_" : " ");
            }
        }
    }

    public static Boundaries constructMaze(MazeBoard board, MazeTheme<? extends MazeGroundDecoration, ? extends MazeWallDecoration> theme, int scale, Instance instance, Pos position) {
        var grid = board.getBoard();
        var groundDecoration = theme.getGroundDecoration();
        var wallDecoration = theme.getWallDecoration();

        var batch = new AbsoluteBlockBatch();

        groundDecoration.createGround(batch, position.sub(0,1,0), scale*grid[0].length, scale*grid.length);

        for(int i = 0; i < grid[0].length; i++) {
            var pos = position.add(i*scale, 0, 0);
            wallDecoration.createBottomHorizontalWall(batch, pos);
        }

        for(int y = 0; y < grid.length; y++) {
            wallDecoration.createRightVerticalWall(batch, position.add(0, 0, y*scale));

            for(int x = 0; x < grid[y].length; x++) {
                var cell = grid[y][x];
                var pos = position.add(x*scale, 0, y*scale);

                var bottom = y+1 >= grid.length;
                var south = cell == WallDirection.SOUTH || bottom;
                var south2 = x+1 < grid[y].length && grid[y][x+1] == WallDirection.SOUTH || bottom;
                var east = cell == WallDirection.EAST || x+1 >= grid[y].length;

                if(south) {
                    wallDecoration.createTopHorizontalWall(batch, pos);
                }
                if(east) {
                    wallDecoration.createLeftVerticalWall(batch, pos);
                } else if(south && south2) {
                    wallDecoration.createTopHorizontalWall(batch, pos);
                }
            }
        }

        batch.apply(instance, null);

        return new Boundaries(position.x(), position.y(), position.z(), position.x() + (grid[0].length * scale), 15, position.z() + (grid.length * scale));
    }

}
