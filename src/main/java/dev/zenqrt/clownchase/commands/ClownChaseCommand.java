package dev.zenqrt.clownchase.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.game.GameSettings;
import dev.zenqrt.clownchase.map.ClownChaseMap;
import dev.zenqrt.clownchase.map.MapManager;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ClownChaseCommand {

    public static void register(Commands commands, GameManager gameManager, MapManager mapManager) {
        commands.register(
                Commands.literal("clownchase")
                        .then(Commands.literal("autojoin")
                                .executes(context -> onGameAutoJoin(context.getSource(), gameManager)))
                        .then(Commands.literal("admin").requires(source -> source.getSender().isOp())
                                .then(Commands.literal("join")
                                        .then(GameIdArgumentType()
                                                .executes(context -> onGameJoin(context.getSource(), gameManager, getGameIdArgument(context)))))
                                .then(Commands.literal("create")
                                        .then(Commands.argument("map", StringArgumentType.word()).suggests((_, builder) -> mapSuggestions(mapManager, builder))
                                                .then(Commands.argument("game_time", IntegerArgumentType.integer(0))
                                                        .then(Commands.argument("min_players", IntegerArgumentType.integer(0))
                                                                .then(Commands.argument("max_players", IntegerArgumentType.integer(0))
                                                                        .executes(context -> onGameCreate(context.getSource(), gameManager, mapManager, context.getArgument("map", String.class), context.getArgument("game_time", Integer.class), context.getArgument("min_players", Integer.class), context.getArgument("max_players", Integer.class))))))))
                                .then(Commands.literal("list")
                                        .executes(context -> onGameList(context.getSource(), gameManager)))

                                .then(Commands.literal("info")
                                        .then(GameIdArgumentType()
                                                .executes(context -> onGameInfo(context.getSource(), gameManager, getGameIdArgument(context))))
                                        .requires(source -> source.getExecutor() instanceof Player)
                                        .executes(context -> findSourceGameId(context.getSource(), gameManager).map(gameId -> onGameInfo(context.getSource(), gameManager, gameId)).orElse(0)))
                                .then(Commands.literal("state")
                                        .then(Commands.literal("next")
                                                .then(GameIdArgumentType()
                                                        .executes(context -> onGameStateNext(context.getSource(), gameManager, getGameIdArgument(context))))
                                                .requires(source -> source.getExecutor() instanceof Player)
                                                .executes(context -> findSourceGameId(context.getSource(), gameManager).map(gameId -> onGameStateNext(context.getSource(), gameManager, gameId)).orElse(0)))
                                        .then(Commands.literal("prev")
                                                .then(GameIdArgumentType()
                                                        .executes(context -> onGameStatePrevious(context.getSource(), gameManager, getGameIdArgument(context))))
                                                .requires(source -> source.getExecutor() instanceof Player)
                                                .executes(context -> findSourceGameId(context.getSource(), gameManager).map(gameId -> onGameStatePrevious(context.getSource(), gameManager, gameId)).orElse(0)))
                                )
                        )
                        .build(), Collections.singletonList("cc")
        );
    }

    private static CompletableFuture<Suggestions> mapSuggestions(MapManager mapManager, final SuggestionsBuilder builder) {
        mapManager.getMaps().keySet()
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Integer> GameIdArgumentType() {
        return Commands.argument("game_id", IntegerArgumentType.integer());
    }

    private static int getGameIdArgument(CommandContext<CommandSourceStack> context) {
        return context.getArgument("game_id", Integer.class);
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

    private static int tryJoinGame(CommandSourceStack source, GameManager gameManager, ClownChaseGame game) {
        source.getSender().sendMessage(Component.text("Joining game " + game.getId() + "...", NamedTextColor.GRAY));

        Optional<ClownChasePlayer> gamePlayerOptional = gameManager.findPlayer(source.getExecutor().getUniqueId());

        if (gamePlayerOptional.isEmpty()) {
            source.getSender().sendMessage(Component.text("Could not find game player data!", TextColorPresets.ERROR));
            return 0;
        } else {
            ClownChasePlayer gamePlayer = gamePlayerOptional.get();
            ClownChaseGame existingGame = gamePlayer.getGame();

            if (existingGame != null)
                existingGame.removePlayer(gamePlayer);

            game.addPlayer(gamePlayer);
            gamePlayer.setGame(game);

            source.getSender().sendMessage(Component.text("Joined game " + game.getId(), NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        }
    }

    private static int onGameAutoJoin(CommandSourceStack source, GameManager gameManager) {
        Optional<ClownChasePlayer> gamePlayerOptional = gameManager.findPlayer(source.getExecutor().getUniqueId());

        if (gamePlayerOptional.isEmpty()) {
            source.getSender().sendMessage(Component.text("Could not find game player data!", TextColorPresets.ERROR));
            return 0;
        } else {
            ClownChasePlayer gamePlayer = gamePlayerOptional.get();
            ClownChaseGame existingGame = gamePlayer.getGame();

            if (existingGame != null) {
                source.getSender().sendMessage(Component.text("You are already in a game!", TextColorPresets.ERROR));
                return 0;
            }

            Optional<ClownChaseGame> gameOptional = gameManager.findAvailableGame();

            if (gameOptional.isEmpty()) {
                source.getSender().sendMessage(Component.text("Could not find an available game!", TextColorPresets.ERROR));
                return 0;
            }

            ClownChaseGame game = gameOptional.get();

            source.getSender().sendMessage(Component.text("Joining game...", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
            game.addPlayer(gamePlayer);
            gamePlayer.setGame(game);

            return Command.SINGLE_SUCCESS;
        }
    }

    private static int onGameCreate(CommandSourceStack source, GameManager gameManager, MapManager mapManager, String mapId, int gameTime, int minPlayers, int maxPlayers) {
        source.getSender().sendMessage(Component.text("Creating game...", NamedTextColor.GRAY));

        Optional<ClownChaseMap> mapOptional = mapManager.findMap(mapId);

        if (mapOptional.isEmpty()) {
            source.getSender().sendMessage(Component.text("Could not find map " + mapId, TextColorPresets.ERROR));
            return 0;
        }

        ClownChaseMap map = mapOptional.get();

        GameSettings settings = new GameSettings(minPlayers, maxPlayers, gameTime, 6);
        ClownChaseGame game = gameManager.createGame(map, settings);

        source.getSender().sendMessage(Component.text("Created game with id " + game.getId(), NamedTextColor.GREEN));
        source.getSender().sendMessage(Component.text("Starting game...", NamedTextColor.GRAY));

        game.start();

        return tryJoinGame(source, gameManager, game);
    }

    private static int onGameJoin(CommandSourceStack source, GameManager gameManager, int gameId) {
        gameManager.findGame(gameId).ifPresentOrElse(
                game -> tryJoinGame(source, gameManager, game),
                () -> source.getSender().sendMessage(Component.text("Invalid game id " + gameId + "!", TextColorPresets.ERROR)));

        return Command.SINGLE_SUCCESS;
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
                    game.getCurrentState().end();
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
