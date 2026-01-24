/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 21:01
 */
public abstract class BlockStarlightNetwork extends BlockContainer {

    // MapColor constructor not available in 1.7.10, use Material-only constructor
    public BlockStarlightNetwork(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        BlockPos pos = new BlockPos(x, y, z);
        TileNetwork teN = MiscUtils.getTileAt(worldIn, x, y, z, TileNetwork.class);
        if (teN != null) {
            teN.onBreak();
        }

        TileEntity inv = MiscUtils.getTileAt(worldIn, x, y, z, TileEntity.class);
        if (inv != null && !worldIn.isRemote) {
            // 1.7.10 uses ISidedInventory instead of CapabilityItemHandler
            if (inv instanceof ISidedInventory) {
                ItemUtils.dropInventory((ISidedInventory) inv, worldIn, pos);
            } else if (inv instanceof net.minecraft.inventory.IInventory) {
                ItemUtils.dropInventory((net.minecraft.inventory.IInventory) inv, worldIn, pos);
            }
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

}
