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
import net.minecraft.util.EnumFacing;

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
        Block state = e.worldObj.getBlock(at);
        if (!(state instanceof FluidBlockLiquidStarlight)) {
            return false;
        }
        if (!((FluidBlockLiquidStarlight) state).isSourceBlock(e.worldObj, at)) {
            return false;
        }
        state = e.worldObj.getBlock(at.down());
        return state.isSideSolid(e.worldObj, at.down(), EnumFacing.UP);
    }

}
