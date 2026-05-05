package dev.zenqrt.clownchase.utils.world;

import io.papermc.paper.math.Position;

public final class PositionUtils {

    private PositionUtils() {}

    public static double distance(Position a, Position b) {
        return Math.sqrt(Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2) + Math.pow(a.z() - b.z(), 2));
    }

}
