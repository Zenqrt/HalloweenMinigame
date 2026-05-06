package dev.zenqrt.clownchase.maze;

import dev.zenqrt.clownchase.maze.theme.MazeTheme;
import dev.zenqrt.clownchase.maze.theme.ground.MazeGroundDecoration;
import dev.zenqrt.clownchase.maze.theme.wall.MazeWallDecoration;
import dev.zenqrt.clownchase.maze.theme.wall.WallDirection;
import dev.zenqrt.clownchase.world.block.BlockBatch;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.bukkit.World;

public final class MazeBuilder {

    private static final int GROUND_DEPTH = 3;

    public static <G extends MazeGroundDecoration, W extends MazeWallDecoration> void constructMaze(MazeBoard board, MazeTheme<G, W> theme, int scale, World world, BlockPosition origin) {
        BlockBatch batch = new BlockBatch();

        theme.groundDecoration().createGround(batch, Position.BLOCK_ZERO.offset(0, -1, 0), scale * board.getDimensionX(), scale * board.getDimensionY(), GROUND_DEPTH);

//        for (int y = 0; y < board.getDimensionY(); y++) {
//
//        }

        for (int x = 0; x < board.getDimensionX(); x++) {
            theme.wallDecoration().createBottomHorizontalWall(batch, Position.BLOCK_ZERO.offset(x*scale, 0, 0));
        }

        for (int y = 0; y < board.getDimensionY(); y++) {
            theme.wallDecoration().createRightVerticalWall(batch, Position.BLOCK_ZERO.offset(0, 0, y*scale));

            for (int x = 0; x < board.getDimensionX(); x++) {
                int cell = board.getBlock(x, y);

                boolean bottom = y+1 >= board.getDimensionY();
                boolean south = cell == WallDirection.SOUTH || bottom;
                boolean south2 = x+1 < board.getDimensionX() && board.getBlock(x+1, y) == WallDirection.SOUTH || bottom;
                boolean east = cell == WallDirection.EAST || x+1 >= board.getDimensionX();

                BlockPosition position = Position.BLOCK_ZERO.offset(x*scale, 0, y*scale);

                if (south) {
                    theme.wallDecoration().createTopHorizontalWall(batch, position);
                }

                if (east) {
                    theme.wallDecoration().createLeftVerticalWall(batch, position);
                } else if (south && south2) {
                    theme.wallDecoration().createTopHorizontalWall(batch, position);
                }
            }
        }

        batch.apply(world, origin);
    }

}
