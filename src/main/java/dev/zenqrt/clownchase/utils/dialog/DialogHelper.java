package dev.zenqrt.clownchase.utils.dialog;

import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.event.ClickCallback;

public final class DialogHelper {

    private DialogHelper() {}

    @SuppressWarnings("UnstableApiUsage")
    public static DialogAction.CustomClickAction handledCustomClick(CustomDialogActionCallback callback, ClickCallback.Options options) {
        return DialogAction.customClick(callback, options);
    }

    public static int parseIntField(String label, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " must be an integer (got '" + input + "')");
        }
    }

}
