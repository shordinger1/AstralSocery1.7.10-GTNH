/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

/**
 * Compatibility class for ChunkPos (introduced in Minecraft 1.9+).
 * Provides an immutable holder for chunk x, z coordinates.
 *
 * In 1.7.10, chunk coordinates are typically handled as raw chunkX, chunkZ values.
 * This class bridges the API difference.
 */
public class ChunkPos {

    /** The chunk's X coordinate (block coordinate >> 4) */
    public final int x;
    /** The chunk's Z coordinate (block coordinate >> 4) */
    public final int z;

    /**
     * Create a new ChunkPos.
     */
    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Create a ChunkPos from a BlockPos.
     */
    public ChunkPos(BlockPos pos) {
        this.x = pos.getX() >> 4;
        this.z = pos.getZ() >> 4;

    }

    /**
     * Get the chunk X coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the chunk Z coordinate.
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Get the block X coordinate of this chunk's origin (lowest x).
     */
    public int getXStart() {
        return this.x << 4;
    }

    /**
     * Get the block Z coordinate of this chunk's origin (lowest z).
     */
    public int getZStart() {
        return this.z << 4;
    }

    /**
     * Get the block X coordinate of this chunk's end (highest x).
     */
    public int getXEnd() {
        return (this.x << 4) + 15;
    }

    /**
     * Get the block Z coordinate of this chunk's end (highest z).
     */
    public int getZEnd() {
        return (this.z << 4) + 15;
    }

    /**
     * Get the number of blocks between this chunk and another.
     */
    public int getDistanceSq(ChunkPos other) {
        int dx = this.x - other.x;
        int dz = this.z - other.z;
        return dx * dx + dz * dz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChunkPos)) return false;
        ChunkPos other = (ChunkPos) obj;
        return this.x == other.x && this.z == other.z;
    }

    @Override
    public int hashCode() {
        return (this.x * 31) + this.z;
    }

    @Override
    public String toString() {
        return "ChunkPos{x=" + this.x + ", z=" + this.z + "}";
    }

    /**
     * Create a ChunkPos from block coordinates.
     */
    public static ChunkPos fromBlockCoords(int blockX, int blockZ) {
        return new ChunkPos(blockX >> 4, blockZ >> 4);
    }
}
