/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IBlockColor interface for block color handlers
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Migration interface for 1.12.2 IBlockColor
 * In 1.7.10, this interface provides compatibility for block color handling
 */
public interface IBlockColor {

    /**
     * Return the color multiplier for the block
     *
     * @param block     The block
     * @param metadata  The block metadata
     * @param world     The block access
     * @param pos       The block position
     * @param tintIndex The tint index
     * @return The color multiplier
     */
    int colorMultiplier(Block block, int metadata, IBlockAccess world, BlockPos pos, int tintIndex);
}
