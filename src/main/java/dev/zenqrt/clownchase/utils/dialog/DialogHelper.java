package dev.zenqrt.clownchase.utils.dialog;

import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.event.ClickCallback;

public final class DialogHelper {

    private DialogHelper() {}

    @SuppressWarnings("UnstableApiUsage")
    public static DialogAction.CustomClickAction handledCustomClick(CustomDialogActionCallback callback, ClickCallback.Options options) {
        return DialogAction.customClick(callback, options);
    }

}
