package dev.zenqrt.clownchase.utils.world;

import io.papermc.paper.math.Position;
import org.bukkit.block.BlockFace;

public final class PositionUtils {

    private PositionUtils() {}

    @SuppressWarnings("UnstableApiUsage")
    public static double distance(Position a, Position b) {
        return Math.sqrt(Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2) + Math.pow(a.z() - b.z(), 2));
    }

    public static BlockFace fromYaw(float yaw) {
        if (yaw >= -45 && yaw < 45)
            return BlockFace.SOUTH;
        else if (yaw >= 45 && yaw < 135)
            return BlockFace.WEST;
        else if (yaw >= 135 && yaw < 225)
            return BlockFace.NORTH;
        else
            return BlockFace.EAST;
    }

}
