/*******************************************************************************
 * Helper class for EntitySpectralTool AI Task
 * 1.7.10 compatibility layer - replaces lambdas with anonymous classes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

public class EntitySpectralToolAIHelpers {

    // BlockStateCheck for finding breakable blocks with pickaxe
    public static final BlockStateCheck.WorldSpecific PICKAXE_CHECK = new BlockStateCheck.WorldSpecific() {

        @Override
        public boolean isStateValid(World world, BlockPos pos, Block block, int metadata) {
            // 1.7.10: Check conditions manually
            if (world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) != null) {
                return false;
            }
            // 1.7.10: getBlock and getBlockHardness with coordinates
            float hardness = block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
            if (hardness == -1 || hardness > 10) {
                return false;
            }
            // Check if tool can break it
            return MiscUtils.canToolBreakBlockWithoutPlayer(world, pos, block, new ItemStack(Items.diamond_pickaxe));
        }
    };

    // BlockStateCheck for finding breakable logs/leaves with axe
    public static final BlockStateCheck.WorldSpecific AXE_CHECK = new BlockStateCheck.WorldSpecific() {

        @Override
        public boolean isStateValid(World world, BlockPos pos, Block block, int metadata) {
            if (world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) != null) {
                return false;
            }
            float hardness = block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
            if (hardness == -1 || hardness > 10) {
                return false;
            }
            // 1.7.10: Check if wood or leaves
            if (block.isWood(world, pos.getX(), pos.getY(), pos.getZ())
                || block.isLeaves(world, pos.getX(), pos.getY(), pos.getZ())) {
                return MiscUtils.canToolBreakBlockWithoutPlayer(world, pos, block, new ItemStack(Items.diamond_axe));
            }
            return false;
        }
    };

    // BlockStateCheck for finding breakable blocks above starting position
    public static final BlockStateCheck.WorldSpecific PICKAXE_CHECK_ABOVE = new BlockStateCheck.WorldSpecific() {

        @Override
        public boolean isStateValid(World world, BlockPos pos, Block block, int metadata) {
            if (world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) != null) {
                return false;
            }
            float hardness = block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
            if (hardness == -1 || hardness > 10) {
                return false;
            }
            return MiscUtils.canToolBreakBlockWithoutPlayer(world, pos, block, new ItemStack(Items.diamond_pickaxe));
        }
    };

    private EntitySpectralToolAIHelpers() {}
}
