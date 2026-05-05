package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.base.GameState;
import io.papermc.paper.math.BlockPosition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Objects;
import java.util.Optional;

public final class SpawnClownsGameState extends GameState {

    private final ClownChaseGame game;

    public SpawnClownsGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        for (ClownChasePlayer gamePlayer : game.getPlayers().values()) {
            Player player = gamePlayer.validatePlayer();
            World world = player.getWorld();
            BlockPosition spawn = findAvailableSpawn(world, player.getLocation().toBlock(), 2, 2)
                    .orElseThrow(() -> new RuntimeException("Unable to find available spawn for clown for " + player.getName()));

            ServerLevel level = ((CraftWorld) world).getHandle();
            Clown clown = new Clown(((CraftPlayer) player).getHandle(), level);
            clown.setPos(spawn.x(), spawn.y(), spawn.z());
            clown.setNoAi(true);

            level.addFreshEntity(clown, CreatureSpawnEvent.SpawnReason.CUSTOM);
            this.game.assignClown(gamePlayer, clown);

            AttributeInstance movementSpeed = Objects.requireNonNull(clown.getAttribute(Attributes.MOVEMENT_SPEED), "movementSpeed");
            movementSpeed.setBaseValue(0.32);
        }

        this.game.nextState();
    }

    private Optional<BlockPosition> findAvailableSpawn(World world, BlockPosition origin, int xRadius, int zRadius) {
        /**
         *          X
         *          |
         *          |
         *   X------O------X
         *          |
         *          |
         *          X
         *
         *  X marks valid spawn locations
         *  O marks the origin
         */

        BlockPosition negX = origin.offset(-xRadius, 0, 0);
        BlockPosition posX = origin.offset(xRadius, 0, 0);
        BlockPosition negZ = origin.offset(0, 0, -zRadius);
        BlockPosition posZ = origin.offset(0, 0, zRadius);

        if (world.getBlockAt(negX.blockX(), negX.blockY(), negX.blockZ()).isEmpty()){
            return Optional.of(negX);
        } else if (world.getBlockAt(posX.blockX(), posX.blockY(), posX.blockZ()).isEmpty()){
            return Optional.of(posX);
        } else if (world.getBlockAt(negZ.blockX(), negZ.blockY(), negZ.blockZ()).isEmpty()){
            return Optional.of(negZ);
        } else if (world.getBlockAt(posZ.blockX(), posZ.blockY(), posZ.blockZ()).isEmpty()){
            return Optional.of(posZ);
        }

        return Optional.empty();
    }
}
