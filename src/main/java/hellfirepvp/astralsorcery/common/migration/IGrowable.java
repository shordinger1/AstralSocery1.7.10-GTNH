/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IGrowable interface for block growth (1.12+ API)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Compatibility wrapper for IGrowable.
 * Bridges 1.12+ BlockPos-based API with 1.7.10 coordinate-based API.
 */
public interface IGrowable {

    /**
     * Check if the block can grow at the given position.
     * 1.12+ API: canGrow(World world, BlockPos pos, Block state, boolean isClient)
     * 1.7.10 API: func_149851_a(World worldIn, int x, int y, int z, boolean isClient)
     */
    default boolean canGrow(World world, BlockPos pos, Block state, boolean isClient) {
        net.minecraft.block.IGrowable growable = asIGrowable(state);
        if (growable != null) {
            return growable.func_149851_a(world, pos.getX(), pos.getY(), pos.getZ(), isClient);
        }
        return false;
    }

    /**
     * Check if the block can grow (with random).
     * 1.7.10 API: func_149852_a(World worldIn, Random random, int x, int y, int z)
     */
    default boolean canUseBonemeal(World world, Random rand, BlockPos pos, Block state) {
        net.minecraft.block.IGrowable growable = asIGrowable(state);
        if (growable != null) {
            return growable.func_149852_a(world, rand, pos.getX(), pos.getY(), pos.getZ());
        }
        return false;
    }

    /**
     * Grow the block at the given position.
     * 1.12+ API: grow(World world, Random rand, BlockPos pos, Block state)
     * 1.7.10 API: func_149853_b(World worldIn, Random random, int x, int y, int z)
     */
    default void grow(World world, Random rand, BlockPos pos, Block state) {
        net.minecraft.block.IGrowable growable = asIGrowable(state);
        if (growable != null) {
            growable.func_149853_b(world, rand, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    /**
     * Convert the Block to the native 1.7.10 IGrowable interface.
     */
    default net.minecraft.block.IGrowable asIGrowable(Block state) {
        if (state instanceof net.minecraft.block.IGrowable) {
            return (net.minecraft.block.IGrowable) state;
        }
        return null;
    }
}
