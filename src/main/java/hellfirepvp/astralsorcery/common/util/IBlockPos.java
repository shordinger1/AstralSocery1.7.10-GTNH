package hellfirepvp.astralsorcery.common.util;

/**
 * Compatibility interface for IBlockPos (introduced in Minecraft 1.8+).
 * Provides a contract for position holders.
 *
 * In 1.7.10, this extends ChunkCoordinates for compatibility.
 */
public interface IBlockPos {

    /**
     * Get the X coordinate.
     */
    int getX();

    /**
     * Get the Y coordinate.
     */
    int getY();

    /**
     * Get the Z coordinate.
     */
    int getZ();
}
