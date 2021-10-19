package dev.zenqrt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.zenqrt.game.halloween.maze.MazeBoard;
import dev.zenqrt.game.halloween.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.game.halloween.maze.themes.HedgeMazeTheme;
import dev.zenqrt.game.halloween.maze.themes.wall.SolidWallDecoration;
import dev.zenqrt.utils.maze.MazeBuilder;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

@CommandAlias("createmaze")
public class CreateMazeCommand extends BaseCommand {

    @Default
    public void createMaze(Player player, int scale, int x, int y) {
        player.sendMessage("Creating maze...");
        var board = new MazeBoard(x, y);
        var strategy = new RecursiveDivisionStrategy();
        strategy.execute(board);

        var position = player.getPosition();
        MazeBuilder.constructMaze(board, new HedgeMazeTheme(scale, 4, 5), scale, player.getInstance(), new Pos(position.blockX(), position.blockY(), position.blockZ()));
        player.sendMessage("Created maze");
    }

}
