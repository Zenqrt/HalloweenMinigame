package dev.zenqrt.clownchase.entity.candy;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
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

        level.addFreshEntity(this.tagDisplay);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        this.tagDisplay.setPos(x, y + 0.5, z);
    }

    @Override
    public void remove(@NotNull RemovalReason reason, EntityRemoveEvent.@Nullable Cause eventCause) {
        super.remove(reason, eventCause);

        this.tagDisplay.remove(reason, eventCause);
    }
}
