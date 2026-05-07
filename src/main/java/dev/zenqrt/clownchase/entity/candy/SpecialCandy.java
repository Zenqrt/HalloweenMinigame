package dev.zenqrt.clownchase.entity.candy;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SpecialCandy extends Candy {

    private final TextDisplay tagDisplay;

    public SpecialCandy(Level level, Component tag, String textures) {
        this.tagDisplay = new TextDisplay(EntityType.TEXT_DISPLAY, level);

        super(level, textures);

        this.tagDisplay.setText(PaperAdventure.asVanilla(tag));
        this.tagDisplay.setBillboardConstraints(BillboardConstraints.CENTER);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        ClientboundAddEntityPacket addPacket = new ClientboundAddEntityPacket(
                this.tagDisplay.getId(),
                this.tagDisplay.getUUID(),
                this.getX(),
                this.getY() + 0.25,
                this.getZ(),
                this.tagDisplay.getXRot(),
                this.tagDisplay.getYRot(),
                this.tagDisplay.getType(),
                0,
                Vec3.ZERO,
                this.tagDisplay.getYHeadRot()
        );
        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(this.tagDisplay.getId(), Objects.requireNonNull(this.tagDisplay.getEntityData().getNonDefaultValues(), "entityData"));

        ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(List.of(addPacket, dataPacket));
        player.connection.send(bundlePacket);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);

        ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(this.tagDisplay.getId());
        player.connection.send(removePacket);
    }

    @Override
    public void remove(@NotNull RemovalReason reason, EntityRemoveEvent.@Nullable Cause eventCause) {
        super.remove(reason, eventCause);

        this.tagDisplay.remove(reason, eventCause);
    }
}
