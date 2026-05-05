package dev.zenqrt.clownchase.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class GameCommand {

    public static void register(Commands commands, GameManager gameManager) {
        commands.register(
                Commands.literal("game")
                        .then(Commands.literal("list")
                                .executes(context -> onGameList(context.getSource(), gameManager)))
                        .then(Commands.literal("info")
                                .then(Commands.argument("game_id", IntegerArgumentType.integer())
                                        .executes(context -> onGameInfo(context.getSource(), gameManager, context.getArgument("game_id", Integer.class))))
                                .requires(source -> source.getExecutor() instanceof Player)
                                .executes(context -> findSourceGameId(context.getSource(), gameManager).map(gameId -> onGameInfo(context.getSource(), gameManager, gameId)).orElse(0)))
                        .then(Commands.literal("state")
                                .then(Commands.literal("next")
                                        .then(Commands.argument("game_id", IntegerArgumentType.integer())
                                                .executes(context -> onGameStateNext(context.getSource(), gameManager, context.getArgument("game_id", Integer.class))))
                                        .requires(source -> source.getExecutor() instanceof Player)
                                        .executes(context -> findSourceGameId(context.getSource(), gameManager).map(gameId -> onGameStateNext(context.getSource(), gameManager, gameId)).orElse(0)))
                                .then(Commands.literal("prev")
                                        .then(Commands.argument("game_id", IntegerArgumentType.integer())
                                                .executes(context -> onGameStatePrevious(context.getSource(), gameManager, context.getArgument("game_id", Integer.class))))
                                        .requires(source -> source.getExecutor() instanceof Player)
                                        .executes(context -> findSourceGameId(context.getSource(), gameManager).map(gameId -> onGameStatePrevious(context.getSource(), gameManager, gameId)).orElse(0)))
                        )
                        .build()
        );
    }

    private static Optional<Integer> findSourceGameId(CommandSourceStack source, GameManager gameManager) {
        assert source.getExecutor() instanceof Player;

        Optional<ClownChasePlayer> gamePlayerOptional = gameManager.findPlayer(source.getExecutor().getUniqueId());

        if (gamePlayerOptional.isEmpty()) {
            source.getSender().sendMessage(Component.text("Could not find game player data!", TextColorPresets.ERROR));
            return Optional.empty();
        } else {
            ClownChasePlayer gamePlayer = gamePlayerOptional.get();

            if (gamePlayer.getGame() == null) {
                source.getSender().sendMessage(Component.text("You are currently not in a game!", TextColorPresets.ERROR));
                return Optional.empty();
            } else {
                return Optional.of(gamePlayer.getGame().getId());
            }
        }
    }

    private static int onGameInfo(CommandSourceStack source, GameManager gameManager, int gameId) {
        gameManager.findGame(gameId).ifPresentOrElse(
                game -> {
                    Component diagnosticMessage =
                            Component.text("Game " + gameId + " Info:\n", NamedTextColor.GOLD)
                                    .append(Component.text(" State: {state}\n", NamedTextColor.GRAY)
                                            .replaceText(builder -> builder.matchLiteral("{state}").replacement(Component.text(game.getCurrentState().getClass().getSimpleName(), NamedTextColor.AQUA))))
                                    .append(Component.text(" Player Count: {player_count}", NamedTextColor.GRAY)
                                            .replaceText(builder -> builder.matchLiteral("{player_count}").replacement(Component.text(game.getPlayers().size(), NamedTextColor.YELLOW))));

                    source.getSender().sendMessage(diagnosticMessage);

                },
                () -> source.getSender().sendMessage(Component.text("Invalid game id " + gameId + "!", TextColorPresets.ERROR)));
        return Command.SINGLE_SUCCESS;
    }

    private static int onGameList(CommandSourceStack source, GameManager gameManager) {
        Component gamesListMessage =
                Component.text("List of games:\n", NamedTextColor.LIGHT_PURPLE)
                        .append(Component.join(
                                JoinConfiguration.builder().separator(Component.newline()),
                                gameManager.getGames().values().stream()
                                        .map(game -> Component.text("- ", NamedTextColor.DARK_GRAY)
                                                .append(Component.text(game.getId(), NamedTextColor.AQUA)))
                                        .toList()
                        ));

        source.getSender().sendMessage(gamesListMessage);
        return Command.SINGLE_SUCCESS;
    }

    private static int onGameStateNext(CommandSourceStack source, GameManager gameManager, int gameId) {
        gameManager.findGame(gameId).ifPresentOrElse(
                game -> {
                    game.nextState();
                    source.getSender().sendMessage(Component.text("Switching to next state...", NamedTextColor.GRAY));
                },
                () -> source.getSender().sendMessage(Component.text("Invalid game id " + gameId + "!", TextColorPresets.ERROR)));

        return Command.SINGLE_SUCCESS;
    }

    private static int onGameStatePrevious(CommandSourceStack source, GameManager gameManager, int gameId) {
        gameManager.findGame(gameId).ifPresentOrElse(
                game -> {
                    game.previousState();
                    source.getSender().sendMessage(Component.text("Switching to previous state...", NamedTextColor.GRAY));
                },
                () -> source.getSender().sendMessage(Component.text("Invalid game id " + gameId + "!", TextColorPresets.ERROR)));

        return Command.SINGLE_SUCCESS;
    }
}
