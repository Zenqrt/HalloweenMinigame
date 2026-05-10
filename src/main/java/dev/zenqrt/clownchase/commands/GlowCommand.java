package dev.zenqrt.clownchase.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.zenqrt.clownchase.utils.entity.EntitySharedFlags;
import dev.zenqrt.clownchase.utils.entity.EntityUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GlowCommand {

    public static void register(Commands commands) {
        commands.register(
                Commands.literal("glow")
                        .then(Commands.argument("entity", ArgumentTypes.entity())
                                .executes(context -> onGlow(context.getSource(), context.getArgument("entity", EntitySelectorArgumentResolver.class))))
                        .build());
    }

    private static int onGlow(CommandSourceStack source, EntitySelectorArgumentResolver selector) throws CommandSyntaxException {
        if (!(source.getExecutor() instanceof Player player))
            return 0;

        List<Entity> entities = selector.resolve(source).stream()
                .map(bukkitEntity -> ((CraftEntity) bukkitEntity).getHandle())
                .toList();

        List<Packet<? super ClientGamePacketListener>> dataPackets = new ArrayList<>();

        for (Entity entity : entities) {
            List<SynchedEntityData.DataValue<?>> dataValues = Objects.requireNonNullElse(
                    entity.getEntityData().getNonDefaultValues(),
                    new ArrayList<>());

            EntityUtils.addSharedFlag(EntitySharedFlags.GLOW, dataValues);

            ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(entity.getId(), dataValues);
            dataPackets.add(dataPacket);
        }

        ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(dataPackets);

        ((CraftPlayer) player).getHandle().connection.send(bundlePacket);
        return 1;
    }

}
