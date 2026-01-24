/*******************************************************************************
 * Compatibility Class for Vec3i (1.12.2) in 1.7.10
 * In 1.7.10, integer 3D coordinates are represented by ChunkCoordinates.
 * This class provides 1.12.2 API compatibility for 1.7.10.
 ******************************************************************************/

package net.minecraft.util;

/**
 * A 3D integer vector class for compatibility with 1.12.2 Vec3i.
 * Extends ChunkCoordinates which is the 1.7.10 equivalent.
 */
public class Vec3i extends ChunkCoordinates implements Comparable<Vec3i> {

    public Vec3i(int x, int y, int z) {
        super(x, y, z);
    }

    public Vec3i(ChunkCoordinates coords) {
        super(coords.posX, coords.posY, coords.posZ);
    }

    /**
     * Get X coordinate
     */
    @Override
    public int getX() {
        return this.posX;
    }

    /**
     * Get Y coordinate
     */
    @Override
    public int getY() {
        return this.posY;
    }

    /**
     * Get Z coordinate
     */
    @Override
    public int getZ() {
        return this.posZ;
    }

    @Override
    public int compareTo(Vec3i other) {
        if (this.getY() == other.getY()) {
            if (this.getZ() == other.getZ()) {
                return this.getX() - other.getX();
            }
            return this.getZ() - other.getZ();
        }
        return this.getY() - other.getY();
    }
}
