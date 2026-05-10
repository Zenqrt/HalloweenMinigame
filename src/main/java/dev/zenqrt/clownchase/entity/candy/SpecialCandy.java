package dev.zenqrt.clownchase.entity.candy;

import com.destroystokyo.paper.ParticleBuilder;
import dev.zenqrt.clownchase.utils.entity.EntityUtils;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Particle;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class SpecialCandy extends Candy {

    private static final BlockState LIGHT_STATE = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 8);

    private final TextDisplay tagDisplay;

    public SpecialCandy(Level level, Component tag, String textures) {
        this.tagDisplay = new TextDisplay(EntityType.TEXT_DISPLAY, level);

        super(level, textures);

        this.tagDisplay.setText(PaperAdventure.asVanilla(tag));
        this.tagDisplay.setBillboardConstraints(BillboardConstraints.CENTER);
    }

    @Override
    protected void particleTick() {
        createSpecialParticle()
                .location(
                        this.level().getWorld(),
                        this.getX(),
                        this.getY() - 0.25,
                        this.getZ()
                ).spawn();
    }

    private static ParticleBuilder createSpecialParticle() {
        return Particle.REVERSE_PORTAL.builder()
                .offset(0.25, 0.25, 0.25)
                .extra(0)
                .count(1);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        EntityUtils.showEntity(this.tagDisplay, this.getX(), this.getY() + 0.25, this.getZ(), player);

        ClientboundBlockUpdatePacket blockUpdate = new ClientboundBlockUpdatePacket(this.blockPosition(), LIGHT_STATE);
        player.connection.send(blockUpdate);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);

        EntityUtils.hideEntity(this.tagDisplay, player);

        ClientboundBlockUpdatePacket blockUpdate = new ClientboundBlockUpdatePacket(this.blockPosition(), Blocks.AIR.defaultBlockState());
        player.connection.send(blockUpdate);
    }

    @Override
    public void remove(@NotNull RemovalReason reason, EntityRemoveEvent.@Nullable Cause eventCause) {
        super.remove(reason, eventCause);

        this.tagDisplay.remove(reason, eventCause);
    }
}
