package dev.zenqrt.world.block.rule;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PressurePlatePlacementRule extends BlockPlacementRule {

    public PressurePlatePlacementRule(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull Instance instance, @NotNull Point point, @NotNull Block block) {
        return this.getBlock();
    }

    @Override
    public @Nullable Block blockPlace(@NotNull Instance instance, @NotNull Block block, @NotNull BlockFace blockFace, @NotNull Point point, @NotNull Player player) {
        var direction = blockFace.toDirection();
        var belowBlock = instance.getBlock(point.sub(direction.normalX(), direction.normalY(), direction.normalZ()));
        System.out.println("issolid? = " + belowBlock.isSolid());
        return belowBlock.isSolid() ? block : null;
    }
}
