package dev.zenqrt.utils.block;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.BlockFace;

public class BlockUtils {

    public static Point getBlockPlaceOffset(Point point, BlockFace face) {
        var direction = face.toDirection();
        return point.add(direction.normalX(), direction.normalY(), direction.normalZ());
    }

}
