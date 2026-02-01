/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Dynamic color interface for blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.world.IBlockAccess;

/**
 * BlockDynamicColor interface (1.7.10)
 * <p>
 * Allows blocks to dynamically calculate color multipliers during rendering.
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No BlockPos - uses integer coordinates (x, y, z)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>No renderPass parameter in 1.7.10</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * public class BlockMyDynamic extends AstralBaseBlock implements BlockDynamicColor {
 *     {@literal @}Override
 *     public int getColorMultiplier(IBlockAccess world, int x, int y, int z) {
 *         long time = world.getWorldTime();
 *         if ((time / 100) % 2 == 0) {
 *             return 0xFF0000; // Red
 *         } else {
 *             return 0x0000FF; // Blue
 *         }
 *     }
 * }
 * </pre>
 */
public interface BlockDynamicColor {

    /**
     * Get the color multiplier for this block
     * <p>
     * Called during rendering to tint the block's texture.
     * <p>
     * <b>1.7.10 API:</b> Uses world coordinates instead of BlockPos
     *
     * @param world The world (may be null for item rendering)
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return int - Color multiplier in RGB format (0xRRGGBB), or -1 for default
     *
     * @deprecated 1.7.10 version - no IBlockState, no renderPass
     */
    @Deprecated
    int getColorMultiplier(IBlockAccess world, int x, int y, int z);

    /**
     * Get the base color multiplier (without position)
     * <p>
     * Used for item rendering when no position is available.
     *
     * @return int - Color multiplier in RGB format, or -1 for default
     */
    int getBaseColorMultiplier();
}
