package dev.zenqrt.utils.coordinate;

import net.minestom.server.coordinate.Point;

public class PointUtils {

    @SuppressWarnings("all")
    public static <T extends Point> T toCenter(T point) {
        return (T) point
                .withX(point.blockX() + 0.5)
                .withZ(point.blockZ() + 0.5);
    }

}
