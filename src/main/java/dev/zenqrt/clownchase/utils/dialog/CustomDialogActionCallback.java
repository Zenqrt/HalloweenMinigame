package dev.zenqrt.clownchase.utils.dialog;

import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@SuppressWarnings("UnstableApiUsage")
public interface CustomDialogActionCallback extends DialogActionCallback {

    void acceptHandleExceptions(DialogResponseView response, Audience audience);

    @Override
    default void accept(@NotNull DialogResponseView response, @NotNull Audience audience) {
        try {
            acceptHandleExceptions(response, audience);
        } catch (IllegalArgumentException ex) {
            audience.sendMessage(Component.text("Error: " + ex.getMessage(), TextColorPresets.ERROR));
        }
    }
}
