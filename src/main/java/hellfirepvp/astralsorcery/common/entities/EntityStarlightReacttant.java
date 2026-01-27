/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityStarlightReacttant
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:26
 */
public interface EntityStarlightReacttant {

    default public boolean isInLiquidStarlight(Entity e) {
        BlockPos at = new BlockPos(e);
        // 1.7.10: getBlock takes int coordinates
        Block state = e.worldObj.getBlock(at.getX(), at.getY(), at.getZ());
        if (!(state instanceof FluidBlockLiquidStarlight)) {
            return false;
        }
        // 1.7.10: isSourceBlock takes int coordinates
        if (!((FluidBlockLiquidStarlight) state).isSourceBlock(e.worldObj, at.getX(), at.getY(), at.getZ())) {
            return false;
        }
        // 1.7.10: BlockPos.down() exists (we added it)
        BlockPos down = at.down();
        state = e.worldObj.getBlock(down.getX(), down.getY(), down.getZ());
        // 1.7.10: Block.isSideSolid doesn't exist, check if block is solid via material
        return state.getMaterial()
            .isSolid();
    }

}
