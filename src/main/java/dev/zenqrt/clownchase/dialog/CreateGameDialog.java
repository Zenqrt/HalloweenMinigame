package dev.zenqrt.clownchase.dialog;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GameManager;
import dev.zenqrt.clownchase.game.GameSettings;
import dev.zenqrt.clownchase.map.ClownChaseMap;
import dev.zenqrt.clownchase.map.MapManager;
import dev.zenqrt.clownchase.utils.dialog.DialogHelper;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class CreateGameDialog {

    private static final String MAP_ID_KEY = "map_id";
    private static final String GAME_TIME_KEY = "game_time";
    private static final String MIN_PLAYERS_KEY = "min_players";
    private static final String MAX_PLAYERS_KEY = "max_players";
    private static final String JOIN_ON_CREATE_KEY = "join_on_create";

    private CreateGameDialog() {}

    @SuppressWarnings("UnstableApiUsage")
    public static Dialog create(UUID executorUuid, GameManager gameManager, MapManager mapManager) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Create Game", TextColorPresets.TEXT).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED))
                        .inputs(List.of(
                                DialogInput.singleOption(MAP_ID_KEY, Component.text("Map"),
                                        mapManager.getMaps().entrySet().stream()
                                                .map(entry -> SingleOptionDialogInput.OptionEntry.create(
                                                        entry.getKey(),
                                                        Component.text(entry.getValue().displayName(), NamedTextColor.AQUA),
                                                        false
                                                )).toList()
                                ).build(),
                                DialogInput.text(GAME_TIME_KEY, Component.text("Game Time")).build(),
                                DialogInput.text(MIN_PLAYERS_KEY, Component.text("Minimum Players")).build(),
                                DialogInput.text(MAX_PLAYERS_KEY, Component.text("Maximum Players")).build(),
                                DialogInput.bool(JOIN_ON_CREATE_KEY, Component.text("Join on create?")).build()
                        ))
                        .build())
                .type(DialogType.notice(
                        ActionButton.builder(Component.text("Create game"))
                                .action(DialogHelper.handledCustomClick(
                                        (response, audience) -> {
                                            String mapId = response.getText(MAP_ID_KEY);
                                            Optional<ClownChaseMap> mapOptional = mapManager.findMap(mapId);

                                            if (mapOptional.isEmpty())
                                                throw new IllegalArgumentException("Unknown map id '" + mapId + "'");

                                            boolean shouldJoin = Boolean.TRUE.equals(response.getBoolean(JOIN_ON_CREATE_KEY));  // intellij really wanted me to do this

                                            ClownChaseMap map = mapOptional.get();
                                            int gameTime = parseIntField("Game time", response.getText(GAME_TIME_KEY));
                                            int minPlayers = parseIntField("Minimum players", response.getText(MIN_PLAYERS_KEY));
                                            int maxPlayers = parseIntField("Maximum players", response.getText(MAX_PLAYERS_KEY));

                                            GameSettings gameSettings = new GameSettings(minPlayers, maxPlayers, gameTime, 6);

                                            audience.sendMessage(Component.text("Creating game...", NamedTextColor.GRAY));

                                            ClownChaseGame game = gameManager.createGame(map, gameSettings);
                                            game.start();

                                            audience.sendMessage(Component.text("Done!"));

                                            if (shouldJoin) {
                                                gameManager.findPlayer(executorUuid)
                                                        .ifPresentOrElse(
                                                                gamePlayer -> gameManager.joinGame(gamePlayer, game),
                                                                () -> audience.sendMessage(Component.text("Could not find your game player profile!", NamedTextColor.RED))
                                                        );
                                            }
                                        },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build()
                )));
    }

    private static int parseIntField(String label, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " must be an integer (got '" + input + "')");
        }
    }

}
