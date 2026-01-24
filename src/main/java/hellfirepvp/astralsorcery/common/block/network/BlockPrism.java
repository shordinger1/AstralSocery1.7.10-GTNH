/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTest2
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:37
 */
public class BlockPrism extends BlockStarlightNetwork implements CrystalPropertyItem {

    public BlockPrism() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypeGlass);
        setResistance(12.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        list.add(stack);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, getMaxSize(stack));
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        ForgeDirection facing = ForgeDirection.getOrientation(metadata % 6);
        switch (facing) {
            case NORTH:
                this.setBlockBounds(0.1875F, 0.1875F, 0F, 0.8125F, 0.8125F, 0.875F);
                break;
            case SOUTH:
                this.setBlockBounds(0.1875F, 0.1875F, 0.125F, 0.8125F, 0.8125F, 1F);
                break;
            case WEST:
                this.setBlockBounds(0F, 0.1875F, 0.1875F, 0.875F, 0.8125F, 0.8125F);
                break;
            case EAST:
                this.setBlockBounds(0.125F, 0.1875F, 0.1875F, 1F, 0.8125F, 0.8125F);
                break;
            case UP:
                this.setBlockBounds(0.1875F, 0.125F, 0.1875F, 0.8125F, 1F, 0.8125F);
                break;
            case DOWN:
            default:
                this.setBlockBounds(0.1875F, 0F, 0.1875F, 0.8125F, 0.875F, 0.8125F);
                break;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        ForgeDirection facing = ForgeDirection.getOrientation(metadata % 6);
        switch (facing) {
            case NORTH:
                return AxisAlignedBB.getBoundingBox(x + 0.1875, y + 0.1875, z, x + 0.8125, y + 0.8125, z + 0.875);
            case SOUTH:
                return AxisAlignedBB.getBoundingBox(x + 0.1875, y + 0.1875, z + 0.125, x + 0.8125, y + 0.8125, z + 1);
            case WEST:
                return AxisAlignedBB.getBoundingBox(x, y + 0.1875, z + 0.1875, x + 0.875, y + 0.8125, z + 0.8125);
            case EAST:
                return AxisAlignedBB.getBoundingBox(x + 0.125, y + 0.1875, z + 0.1875, x + 1, y + 0.8125, z + 0.8125);
            case UP:
                return AxisAlignedBB.getBoundingBox(x + 0.1875, y + 0.125, z + 0.1875, x + 0.8125, y + 1, z + 0.8125);
            case DOWN:
            default:
                return AxisAlignedBB.getBoundingBox(x + 0.1875, y, z + 0.1875, x + 0.8125, y + 0.875, z + 0.8125);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
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
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ItemStack stack = super.getPickBlock(target, world, x, y, z);
        TileCrystalPrismLens lens = MiscUtils.getTileAt(world, x, y, z, TileCrystalPrismLens.class);
        if (lens != null && lens.getCrystalProperties() != null) {
            CrystalProperties.applyCrystalProperties(stack, lens.getCrystalProperties());
        } else {
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        }
        return stack;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileCrystalPrismLens lens = MiscUtils.getTileAt(worldIn, x, y, z, TileCrystalPrismLens.class);
        if (lens != null && !worldIn.isRemote) {
            ItemStack drop;
            if (lens.getLensColor() != null) {
                drop = lens.getLensColor()
                    .asStack();
                ItemUtils.dropItemNaturally(worldIn, x + 0.5, y + 0.5, z + 0.5, drop);
            }

            drop = new ItemStack(BlocksAS.lensPrism);
            if (lens.getCrystalProperties() != null) {
                CrystalProperties.applyCrystalProperties(drop, lens.getCrystalProperties());
            } else {
                CrystalProperties.applyCrystalProperties(drop, new CrystalProperties(1, 0, 0, 0, -1));
            }
            ItemUtils.dropItemNaturally(worldIn, x + 0.5, y + 0.5, z + 0.5, drop);
        }

        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            TileCrystalPrismLens lens = MiscUtils.getTileAt(worldIn, x, y, z, TileCrystalPrismLens.class);
            if (lens != null && lens.getLensColor() != null) {
                ItemStack drop = lens.getLensColor()
                    .asStack();
                ItemUtils.dropItemNaturally(worldIn, x + 0.5, y + 0.5, z + 0.5, drop);
                // 1.7.10: SoundHelper.playSoundAround not available, skipping sound
                lens.setLensColor(null);
                return true;
            }
        }
        return false;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileCrystalPrismLens();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TileCrystalPrismLens te = MiscUtils.getTileAt(worldIn, x, y, z, TileCrystalPrismLens.class);
        if (te == null) return;
        te.onPlace(CrystalProperties.getCrystalProperties(stack));
    }

}
