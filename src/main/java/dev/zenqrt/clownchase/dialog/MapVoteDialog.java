package dev.zenqrt.clownchase.dialog;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.map.ClownChaseMap;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Map;

public final class MapVoteDialog {

    private static final String MAP_VOTE_TITLE = "server.dialog.map_vote.title";

//    public static Dialog createDialog(ClownChaseGame game, Map<String, ClownChaseMap> maps) {
//        return Dialog.create(builder -> builder.empty()
//                .base(DialogBase.builder(Component.translatable(MAP_VOTE_TITLE, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
//                        .body(List.of())
//                        .inputs(maps.entrySet().stream()
//                                .map(entry -> DialogInput.singleOption(entry.getKey(), Component.text(entry.getValue().displayName(), NamedTextColor.AQUA)))))
//                        .build())
//                .type(DialogType.notice())
//        );
//    }

}
