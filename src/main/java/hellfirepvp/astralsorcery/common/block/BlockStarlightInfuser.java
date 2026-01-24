/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.block.network.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:05
 */
public class BlockStarlightInfuser extends BlockStarlightNetwork implements BlockStructureObserver {

    private static final AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0D, 0D, 0D, 1D, 12D / 16D, 1D);

    public BlockStarlightInfuser() {
        super(Material.rock);
        setHardness(1.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setStepSound(Block.soundTypePiston);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0F, 0F, 0F, 1F, 12F / 16F, 1F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return box;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return box;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileStarlightInfuser infuser = MiscUtils.getTileAt(worldIn, x, y, z, TileStarlightInfuser.class);
            if (infuser != null) {
                ItemStack held = playerIn.getCurrentEquippedItem();
                infuser.onInteract(playerIn, held);
            }
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        if (!worldIn.isRemote) {
            TileStarlightInfuser infuser = MiscUtils.getTileAt(worldIn, x, y, z, TileStarlightInfuser.class);
            if (infuser != null) {
                ItemStack input = infuser.getInputStack();
                if (input != null && input.stackSize > 0) {
                    ItemUtils.dropItemNaturally(worldIn, x + 0.5, y + 1, z + 0.5, input);
                }
            }
        }

        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStarlightInfuser();
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }
}
