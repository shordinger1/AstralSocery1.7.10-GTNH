/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * BlockColors class for block color handlers
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.block.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.color.BlockColors
 * In 1.7.10: Different color system (IColoredBlock)
 */
public class BlockColors {

    public BlockColors() {}

    public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex) {
        return 0xFFFFFF;
    }

    /**
     * Register a block color handler
     * In 1.7.10, this compatibility layer doesn't actually register color handlers
     * Color handling is done through IItemRenderer implementations
     * 
     * @param colorHandler The color handler (not used in 1.7.10)
     * @param block        The block to register for
     */
    public void registerBlockColorHandler(BlockDynamicColor colorHandler, Block block) {
        // 1.7.10: No actual color registration here
        // Colors are handled through item rendering system
    }
}
