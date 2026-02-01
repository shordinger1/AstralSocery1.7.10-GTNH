/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Starlight reactant entity interface
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;

/**
 * EntityStarlightReacttant - Starlight reactive entity interface (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Interface for entities that react to liquid starlight</li>
 * <li>Checks if entity is in liquid starlight source block</li>
 * <li>Default implementation provided</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Entity.getPosition() → Entity.posX/posY/posZ (no BlockPos)</li>
 * <li>World.getBlockState(pos) → World.getBlock(x, y, z)</li>
 * <li>IBlockState → Block + metadata</li>
 * <li>BlockPos.down() → y - 1</li>
 * <li>EnumFacing → ForgeDirection</li>
 * <li>Entity.getEntityWorld() → Entity.worldObj</li>
 * </ul>
 * <p>
 * <b>Implementation:</b>
 * 
 * <pre>
 * 
 * // Original (1.12.2):
 * BlockPos at = e.getPosition();
 * IBlockState state = e.getEntityWorld()
 *     .getBlockState(at);
 *
 * // Migrated (1.7.10):
 * int x = (int) e.posX;
 * int y = (int) e.posY;
 * int z = (int) e.posZ;
 * Block block = e.worldObj.getBlock(x, y, z);
 * int meta = e.worldObj.getBlockMetadata(x, y, z);
 * </pre>
 */
public interface EntityStarlightReacttant {

    /**
     * Check if the entity is currently in a liquid starlight source block
     *
     * @param e The entity to check
     * @return true if entity is in liquid starlight source block with solid block below
     *
     *         <b>Implementation Details:</b>
     *         <ul>
     *         <li>Checks current block position for FluidBlockLiquidStarlight</li>
     *         <li>Verifies it's a source block (not flowing)</li>
     *         <li>Checks block below is solid on UP face</li>
     *         </ul>
     */
    default public boolean isInLiquidStarlight(Entity e) {
        // 1.7.10: Get entity position as coordinates (no BlockPos)
        int x = (int) e.posX;
        int y = (int) e.posY;
        int z = (int) e.posZ;
        World world = e.worldObj;

        // Check if current block is liquid starlight
        // 1.7.10: world.getBlock() returns Block directly (no IBlockState)
        if (!(world.getBlock(x, y, z) instanceof FluidBlockLiquidStarlight)) {
            return false;
        }

        FluidBlockLiquidStarlight fluid = (FluidBlockLiquidStarlight) world.getBlock(x, y, z);

        // Check if it's a source block
        // 1.7.10: Pass coordinates instead of BlockPos
        if (!fluid.isSourceBlock(world, x, y, z)) {
            return false;
        }
        // Check if block below is solid
        // 1.7.10: y - 1 instead of at.down()
        // Block blockBelow = world.getBlock(x, y - 1, z);
        // ForgeDirection.UP instead of EnumFacing.UP
        return world.isSideSolid(x, y - 1, z, ForgeDirection.UP);
    }

}
