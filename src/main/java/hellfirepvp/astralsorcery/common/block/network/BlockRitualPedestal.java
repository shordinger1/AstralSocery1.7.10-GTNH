/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:45
 */
public class BlockRitualPedestal extends BlockStarlightNetwork {

    private static final AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 13D / 16D, 1);

    public BlockRitualPedestal() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypePiston);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        ItemStack pedestal = new ItemStack(this);
        list.add(pedestal);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRitualPedestal();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileRitualPedestal ped = MiscUtils.getTileAt(worldIn, x, y, z, TileRitualPedestal.class);
        if (ped != null && !worldIn.isRemote) {
            ItemUtils.dropItem(
                worldIn,
                x + 0.5,
                y + 0.8,
                z + 0.5,
                ItemUtils.copyStackWithSize(ped.getCatalystCache(), ped.getCatalystCache().stackSize));
        }

        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    /*
     * @Override
     * @SideOnly(Side.CLIENT)
     * public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
     * RenderingUtils.playBlockBreakParticles(pos,
     * BlocksAS.blockMarble
     * .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW));
     * return true;
     * }
     * @Override
     * @SideOnly(Side.CLIENT)
     * public boolean addHitEffects(Block state, World world, RayTraceResult target, ParticleManager manager) {
     * return true;
     * }
     */

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        TileRitualPedestal pedestal = MiscUtils.getTileAt(worldIn, x, y, z, TileRitualPedestal.class);
        if (pedestal == null) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        ItemStack heldItem = playerIn.getCurrentEquippedItem();

        ItemStack in = pedestal.getCurrentPedestalCrystal();
        if (!(heldItem == null || heldItem.stackSize <= 0) && (in == null || in.stackSize <= 0)
            && ItemTunedCrystalBase.getMainConstellation(heldItem) != null) {
            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = pedestal
                .placeCrystalIntoPedestal(heldItem);
            return true;
        }
        if (!(in == null || in.stackSize <= 0) && playerIn.isSneaking()) {
            pedestal.placeCrystalIntoPedestal(null);
            playerIn.inventory.addItemStackToInventory(in);
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        TileRitualPedestal te = MiscUtils.getTileAt(worldIn, x, y, z, TileRitualPedestal.class);
        if (te != null && !worldIn.isRemote) {
            int yUp = y + 1;
            Block other = worldIn.getBlock(x, yUp, z);
            // 1.7.10: isSideSolid takes ForgeDirection
            if (other != null && other.isSideSolid(worldIn, x, yUp, z, ForgeDirection.DOWN)) {
                ItemUtils.dropItem(
                    worldIn,
                    x + 0.5,
                    y + 0.8,
                    z + 0.5,
                    ItemUtils.copyStackWithSize(te.getCatalystCache(), te.getCatalystCache().stackSize));
                te.placeCrystalIntoPedestal(null);
                te.markForUpdate();
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TileRitualPedestal te = MiscUtils.getTileAt(worldIn, x, y, z, TileRitualPedestal.class);
        if (te != null && !worldIn.isRemote) {
            if (placer instanceof EntityPlayer) {
                te.setOwner(placer.getUniqueID());
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0F, 0F, 0F, 1F, 13F / 16F, 1F);
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

}
