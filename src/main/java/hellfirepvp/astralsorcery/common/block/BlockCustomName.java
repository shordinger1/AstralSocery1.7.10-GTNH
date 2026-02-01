/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Custom name interface for ItemBlocks with variant-specific names
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

/**
 * BlockCustomName interface (1.7.10)
 * <p>
 * Interface for blocks that need custom item names based on metadata.
 * <p>
 * <b>Usage:</b>
 * Implement this interface on blocks that have multiple variants with different names.
 * The ItemBlock implementation can then use getIdentifierForMeta() to get the
 * correct name for each variant.
 * <p>
 * <b>Example:</b>
 * 
 * <pre>
 * public class BlockMarble extends AstralBaseBlock implements BlockCustomName {
 *     {@literal @}Override
 *     public String getIdentifierForMeta(int meta) {
 *         String[] names = {"raw", "bricks", "pillar", ...};
 *         return names[meta % names.length];
 *     }
 * }
 * </pre>
 */
public interface BlockCustomName {

    /**
     * Get the identifier for a specific metadata value
     * <p>
     * Used by ItemBlock to get the correct name for this variant.
     * <p>
     * This should return a lowercase string suitable for use in:
     * - Lang file entries (e.g., "tile.blockmarble.raw.name")
     * - Model files (e.g., "astralsorcery:blockMarble_raw")
     * <p>
     *
     * @param meta Block metadata (0-15)
     * @return Identifier string for this metadata value
     */
    String getIdentifierForMeta(int meta);
}
