package dev.zenqrt.clownchase.entity.candy;

import dev.zenqrt.clownchase.utils.EntityUtils;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class SpecialCandy extends Candy {

    private final TextDisplay tagDisplay;

    public SpecialCandy(Level level, Component tag, String textures) {
        this.tagDisplay = new TextDisplay(EntityType.TEXT_DISPLAY, level);

        super(level, textures);

        this.tagDisplay.setText(PaperAdventure.asVanilla(tag));
        this.tagDisplay.setBillboardConstraints(BillboardConstraints.CENTER);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        EntityUtils.showEntity(this.tagDisplay, this.getX(), this.getY() + 0.25, this.getZ(), player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);

        EntityUtils.hideEntity(this.tagDisplay, player);
    }

    @Override
    public void remove(@NotNull RemovalReason reason, EntityRemoveEvent.@Nullable Cause eventCause) {
        super.remove(reason, eventCause);

        this.tagDisplay.remove(reason, eventCause);
    }
}
