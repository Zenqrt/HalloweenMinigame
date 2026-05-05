package dev.zenqrt.clownchase.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.zenqrt.clownchase.maze.MazeBoard;
import dev.zenqrt.clownchase.maze.MazeBuilder;
import dev.zenqrt.clownchase.maze.strategy.RecursiveDivisionStrategy;
import dev.zenqrt.clownchase.maze.theme.SolidMazeTheme;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.block.BlockType;

public final class MazeCommand {

    public static void register(Commands commands) {
        commands.register(Commands.literal("maze")
                .then(Commands.argument("scale", IntegerArgumentType.integer())
                        .executes(context -> {
                            context.getSource().getSender().sendMessage("Generating...");
                            int scale = context.getArgument("scale", Integer.class);
                            MazeBoard board = new MazeBoard(10, 10);

                            new RecursiveDivisionStrategy().execute(board);

                            MazeBuilder.constructMaze(board, new SolidMazeTheme(scale, 4, 5, BlockType.BLACK_CONCRETE.createBlockData(), BlockType.WHITE_CONCRETE.createBlockData()), scale, context.getSource().getLocation().getWorld(), context.getSource().getLocation().toBlock());
                            context.getSource().getSender().sendMessage("Done!");
                            return 1;
                        })).build());
    }

}
