package dev.zenqrt.world.collision;

public class Boundaries {

    private final double minX,minY,minZ,maxX,maxY,maxZ;

    public Boundaries(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        this.maxZ = Math.max(maxZ, minZ);
    }

    public Boundaries scale(double scale) {
        return expand(scale, scale, scale);
    }

    public Boundaries expand(double x, double y, double z) {
        var dx = x / 2;
        var dy = y / 2;
        var dz = z / 2;
        return new Boundaries(
                minX - dx,
                minY - dy,
                minZ - dz,
                maxX + dx,
                maxY + dy,
                maxZ + dz
        );
    }

    public Boundaries shrink(double x, double y, double z) {
        var dx = x / 2;
        var dy = y / 2;
        var dz = z / 2;
        return new Boundaries(
                minX + dx,
                minY + dy,
                minZ + dz,
                maxX - dx,
                maxY - dy,
                maxZ - dz
        );
    }

    public double getSizeX() {
        return maxX - minX;
    }

    public double getSizeY() {
        return maxY - minY;
    }

    public double getSizeZ() {
        return maxZ - minZ;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }
}
