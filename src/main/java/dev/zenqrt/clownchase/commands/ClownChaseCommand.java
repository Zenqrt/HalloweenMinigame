package dev.zenqrt.clownchase.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.zenqrt.clownchase.ClownChasePlugin;
import dev.zenqrt.clownchase.dialog.CreateGameDialog;
import dev.zenqrt.clownchase.exceptions.SimplePaperCommandExceptionType;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.game.GameSettings;
import dev.zenqrt.clownchase.map.ClownChaseMap;
import dev.zenqrt.clownchase.map.MapManager;
import dev.zenqrt.clownchase.utils.text.CommandMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public final class ClownChaseCommand {

    private static final SimplePaperCommandExceptionType NO_MAP_WITH_ID = new SimplePaperCommandExceptionType(Component.text("Could not find map with that id!"));
    private static final SimplePaperCommandExceptionType NO_GAME_PLAYER_PROFILE = new SimplePaperCommandExceptionType(Component.text("Could not find game player profile!"));
    private static final SimplePaperCommandExceptionType NO_GAME_WITH_ID = new SimplePaperCommandExceptionType(Component.text("Could not find game with that id!"));
    private static final SimplePaperCommandExceptionType NO_AVAILABLE_GAME = new SimplePaperCommandExceptionType(Component.text("Could not find an available game!"));
    private static final SimplePaperCommandExceptionType NOT_IN_GAME = new SimplePaperCommandExceptionType(Component.text("You are not in a game!"));
    private static final SimplePaperCommandExceptionType ALREADY_IN_GAME = new SimplePaperCommandExceptionType(Component.text("You are already in a game!"));

    public static void register(Commands commands, ClownChasePlugin plugin, GameManager gameManager, MapManager mapManager) {
        commands.register(
                Commands.literal("clownchase")
                        .then(Commands.literal("lobby")
                                .executes(context -> onLobby(context.getSource(), plugin.getLobbySpawn(), tryGetGamePlayer(context.getSource(), gameManager), gameManager)))
                        .then(Commands.literal("autojoin")
                                .executes(context -> onGameAutoJoin(context.getSource(), tryGetGamePlayer(context.getSource(), gameManager), gameManager)))
                        .then(Commands.literal("game").requires(source -> source.getSender().isOp())
                                .then(Commands.literal("join")
                                        .then(GameIdArgumentType()
                                                .executes(context -> onGameJoin(context.getSource(), tryGetGamePlayer(context.getSource(), gameManager), tryGetGame(getGameIdArgument(context), gameManager)))))
                                .then(Commands.literal("create")
                                        .then(Commands.argument("map", StringArgumentType.word()).suggests((_, builder) -> mapSuggestions(mapManager, builder))
                                                .then(Commands.argument("game_time", IntegerArgumentType.integer(0))
                                                        .then(Commands.argument("min_players", IntegerArgumentType.integer(0))
                                                                .then(Commands.argument("max_players", IntegerArgumentType.integer(0))
                                                                        .executes(context -> onGameCreate(context.getSource(), tryGetGamePlayer(context.getSource(), gameManager), gameManager, mapManager, context.getArgument("map", String.class), context.getArgument("game_time", Integer.class), context.getArgument("min_players", Integer.class), context.getArgument("max_players", Integer.class)))))))
                                        .executes(context -> onGameCreateDialog(context.getSource(), gameManager, mapManager)))
                                .then(Commands.literal("delete")
                                        .then(GameIdArgumentType()
                                                .executes(context -> onGameDelete(context.getSource(), tryGetGame(getGameIdArgument(context), gameManager)))))
                                .then(Commands.literal("list")
                                        .executes(context -> onGameList(context.getSource(), gameManager)))
                                .then(Commands.literal("info")
                                        .then(GameIdArgumentType()
                                                .executes(context -> onGameInfo(context.getSource(), tryGetGame(getGameIdArgument(context), gameManager))))
                                        .requires(source -> source.getExecutor() instanceof Player)
                                        .executes(context -> onGameInfo(context.getSource(), tryGetSourceGame(context.getSource(), gameManager))))
                                .then(Commands.literal("state")
                                        .then(Commands.literal("next")
                                                .then(GameIdArgumentType()
                                                        .executes(context -> onGameStateNext(context.getSource(), tryGetGame(getGameIdArgument(context), gameManager))))
                                                .requires(source -> source.getExecutor() instanceof Player)
                                                .executes(context -> onGameStateNext(context.getSource(), tryGetSourceGame(context.getSource(), gameManager))))
                                        .then(Commands.literal("prev")
                                                .then(GameIdArgumentType()
                                                        .executes(context -> onGameStatePrevious(context.getSource(), tryGetGame(getGameIdArgument(context), gameManager)))
                                                .requires(source -> source.getExecutor() instanceof Player)
                                                .executes(context -> onGameStatePrevious(context.getSource(), tryGetSourceGame(context.getSource(), gameManager)))))))
                        .then(Commands.literal("map").requires(source -> source.getSender().isOp())
                                .then(Commands.literal("list")
                                        .executes(context -> onMapList(context.getSource(), mapManager)))
                                .then(Commands.literal("reload")
                                        .executes(context -> onMapReload(context.getSource(), mapManager))))
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

    private static ClownChaseGame tryGetSourceGame(CommandSourceStack source, GameManager gameManager) throws CommandSyntaxException {
        assert source.getExecutor() instanceof Player;

        ClownChasePlayer gamePlayer = tryGetGamePlayer(source, gameManager);
        ClownChaseGame game = gamePlayer.getGame();

        if (game == null)
            throw NOT_IN_GAME.create();

        return game;
    }

    private static int tryJoinGame(CommandSourceStack source, ClownChasePlayer gamePlayer, ClownChaseGame game) {
        assert source.getExecutor() instanceof Player;

        source.getSender().sendMessage(Component.text("Joining game " + game.getId() + "...", NamedTextColor.GRAY));

        ClownChaseGame existingGame = gamePlayer.getGame();

        if (existingGame != null)
            existingGame.removePlayer(gamePlayer);

        game.addPlayer(gamePlayer);
        gamePlayer.setGame(game);

        return CommandMessages.sendSuccess(source, "Joined game " + game.getId());
    }

    private static ClownChasePlayer tryGetGamePlayer(CommandSourceStack source, GameManager gameManager) throws CommandSyntaxException {
        assert source.getExecutor() instanceof Player;

        return gameManager.findPlayer(source.getExecutor().getUniqueId())
                .orElseThrow(NO_GAME_PLAYER_PROFILE::create);
    }

    private static ClownChaseGame tryGetGame(int gameId, GameManager gameManager) throws CommandSyntaxException {
        return gameManager.findGame(gameId)
                .orElseThrow(NO_GAME_WITH_ID::create);
    }

    private static int onGameAutoJoin(CommandSourceStack source, ClownChasePlayer gamePlayer, GameManager gameManager) throws CommandSyntaxException {
        ClownChaseGame existingGame = gamePlayer.getGame();

        if (existingGame != null)
            throw ALREADY_IN_GAME.create();

        ClownChaseGame game = gameManager.findAvailableGame()
                .orElseThrow(NO_AVAILABLE_GAME::create);

        CommandMessages.sendInfo(source, "Joining game...");

        game.addPlayer(gamePlayer);
        gamePlayer.setGame(game);

        return Command.SINGLE_SUCCESS;
    }

    private static int onGameCreateDialog(CommandSourceStack source, GameManager gameManager, MapManager mapManager) {
        assert source.getExecutor() instanceof Player;

        source.getExecutor().showDialog(
                CreateGameDialog.create(source.getExecutor().getUniqueId(), gameManager, mapManager)
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int onGameCreate(CommandSourceStack source, ClownChasePlayer gamePlayer, GameManager gameManager, MapManager mapManager, String mapId, int gameTime, int minPlayers, int maxPlayers) throws CommandSyntaxException {
        CommandMessages.sendInfo(source, "Creating game...");

        ClownChaseMap map = mapManager.findMap(mapId)
                .orElseThrow(NO_MAP_WITH_ID::create);

        GameSettings settings = new GameSettings(minPlayers, maxPlayers, gameTime, 6);
        ClownChaseGame game = gameManager.createGame(map, settings);

        CommandMessages.sendSuccess(source, "Created game with id " + game.getId());
        CommandMessages.sendInfo(source, "Starting game...");

        game.start();

        return tryJoinGame(source, gamePlayer, game);
    }

    private static int onGameDelete(CommandSourceStack source, ClownChaseGame game) {
        CommandMessages.sendInfo(source, "Deleting game...");

        game.end();

        return CommandMessages.sendSuccess(source, "Deleted game " + game.getId());
    }

    private static int onGameJoin(CommandSourceStack source, ClownChasePlayer gamePlayer, ClownChaseGame game) {
        return tryJoinGame(source, gamePlayer, game);
    }

    private static int onGameInfo(CommandSourceStack source, ClownChaseGame game) {
        Component diagnosticMessage =
                Component.text("Game " + game.getId() + " Info:\n", NamedTextColor.GOLD)
                        .append(Component.text(" State: {state}\n", NamedTextColor.GRAY)
                                .replaceText(builder -> builder.matchLiteral("{state}").replacement(Component.text(game.getCurrentState().getClass().getSimpleName(), NamedTextColor.AQUA))))
                        .append(Component.text(" Player Count: {player_count}", NamedTextColor.GRAY)
                                .replaceText(builder -> builder.matchLiteral("{player_count}").replacement(Component.text(game.getPlayers().size(), NamedTextColor.YELLOW))));

        return CommandMessages.sendResponse(source, diagnosticMessage);
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

        return CommandMessages.sendResponse(source, gamesListMessage);
    }

    private static int onGameStateNext(CommandSourceStack source, ClownChaseGame game) {
        CommandMessages.sendInfo(source, "Switching to next state...");

        game.nextState();

        return Command.SINGLE_SUCCESS;
    }

    private static int onGameStatePrevious(CommandSourceStack source, ClownChaseGame game) {
        CommandMessages.sendInfo(source, "Switching to previous state...");

        game.previousState();

        return Command.SINGLE_SUCCESS;
    }

    private static int onMapList(CommandSourceStack source, MapManager mapManager) {
        Component response =
                Component.join(
                        JoinConfiguration.builder()
                                .prefix(Component.text("\n\nRegistered Maps\n", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                                .separator(Component.newline())
                                .build(),
                        mapManager.getMaps().entrySet().stream()
                                .map(entry -> Component.text("- ", NamedTextColor.DARK_GRAY)
                                        .append(Component.text(entry.getValue().displayName(), NamedTextColor.WHITE))
                                        .append(Component.text(" (" + entry.getKey() + ")", NamedTextColor.GRAY)))
                                .toList()
                );

        return CommandMessages.sendResponse(source, response);
    }

    private static int onMapReload(CommandSourceStack source, MapManager mapManager) {
        CommandMessages.sendInfo(source, "Reloading maps...");

        mapManager.unregisterAllMaps();
        mapManager.loadMaps();

        return CommandMessages.sendSuccess(source, "Done!");
    }

    private static int onLobby(CommandSourceStack source, Location lobbySpawn, ClownChasePlayer gamePlayer, GameManager gameManager) {
        assert source.getExecutor() instanceof Player;

        if (gamePlayer.getGame() != null)
            gameManager.leaveGame(gamePlayer, gamePlayer.getGame());

        source.getExecutor().teleportAsync(lobbySpawn, PlayerTeleportEvent.TeleportCause.COMMAND);

        return Command.SINGLE_SUCCESS;
    }
}
