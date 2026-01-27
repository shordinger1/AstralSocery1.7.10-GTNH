/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
import hellfirepvp.astralsorcery.common.block.BlockVariants;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLens
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:31
 */
public class BlockLens extends BlockStarlightNetwork implements BlockVariants, CrystalPropertyItem {

    private static final AxisAlignedBB boxLensDown = AxisAlignedBB
        .getBoundingBox(2.5D / 16D, 0, 2.5D / 16D, 13.5D / 16D, 14.5D / 16D, 13.5D / 16D);
    private static final AxisAlignedBB boxLensUp = AxisAlignedBB
        .getBoundingBox(2.5D / 16D, 1.5D / 16D, 2.5D / 16D, 13.5D / 16D, 1, 13.5D / 16D);
    private static final AxisAlignedBB boxLensNorth = AxisAlignedBB
        .getBoundingBox(2.5D / 16D, 2.5D / 16D, 0, 13.5D / 16D, 13.5D / 16D, 14.5D / 16D);
    private static final AxisAlignedBB boxLensSouth = AxisAlignedBB
        .getBoundingBox(2.5D / 16D, 2.5D / 16D, 1.5D / 16D, 13.5D / 16D, 13.5D / 16D, 1);
    private static final AxisAlignedBB boxLensEast = AxisAlignedBB
        .getBoundingBox(1.5D / 16D, 2.5D / 16D, 2.5D / 16D, 1, 13.5D / 16D, 13.5D / 16D);
    private static final AxisAlignedBB boxLensWest = AxisAlignedBB
        .getBoundingBox(0, 2.5D / 16D, 2.5D / 16D, 14.5D / 16D, 13.5D / 16D, 13.5D / 16D);

    public BlockLens() {
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
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return side;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 2: // North
                return boxLensNorth;
            case 3: // South
                return boxLensSouth;
            case 4: // West
                return boxLensWest;
            case 5: // East
                return boxLensEast;
            case 1: // Up
                return boxLensUp;
            default: // Down
            case 0:
                return boxLensDown;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 2: // North
                this.setBlockBounds(2.5F / 16F, 2.5F / 16F, 0F, 13.5F / 16F, 13.5F / 16F, 14.5F / 16F);
                break;
            case 3: // South
                this.setBlockBounds(2.5F / 16F, 2.5F / 16F, 1.5F / 16F, 13.5F / 16F, 13.5F / 16F, 1F);
                break;
            case 4: // West
                this.setBlockBounds(0F, 2.5F / 16F, 2.5F / 16F, 14.5F / 16F, 13.5F / 16F, 13.5F / 16F);
                break;
            case 5: // East
                this.setBlockBounds(1.5F / 16F, 2.5F / 16F, 2.5F / 16F, 1F, 13.5F / 16F, 13.5F / 16F);
                break;
            case 1: // Up
                this.setBlockBounds(2.5F / 16F, 1.5F / 16F, 2.5F / 16F, 13.5F / 16F, 1F, 13.5F / 16F);
                break;
            default: // Down
            case 0:
                this.setBlockBounds(2.5F / 16F, 0F, 2.5F / 16F, 13.5F / 16F, 14.5F / 16F, 13.5F / 16F);
                break;
        }
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
    public int getRenderType() {
        return -1; // Use TESR
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileCrystalLens();
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ItemStack stack = new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
        TileCrystalLens lens = MiscUtils.getTileAt(world, x, y, z, TileCrystalLens.class);
        if (lens != null && lens.getCrystalProperties() != null) {
            CrystalProperties.applyCrystalProperties(stack, lens.getCrystalProperties());
        } else {
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        }
        return stack;
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        TileCrystalLens lens = MiscUtils.getTileAt(world, x, y, z, TileCrystalLens.class);
        if (lens != null && !world.isRemote && world.getPlayerEntityByName("placeholder") != null) {
            ItemStack drop;
            if (lens.getLensColor() != null) {
                drop = lens.getLensColor()
                    .asStack();
                ItemUtils.dropItemNaturally(world, x + 0.5, y + 0.5, z + 0.5, drop);
            }

            drop = new ItemStack(BlocksAS.lens);
            if (lens.getCrystalProperties() != null) {
                CrystalProperties.applyCrystalProperties(drop, lens.getCrystalProperties());
            } else {
                CrystalProperties.applyCrystalProperties(drop, new CrystalProperties(1, 0, 0, 0, -1));
            }
            ItemUtils.dropItemNaturally(world, x + 0.5, y + 0.5, z + 0.5, drop);
        }

        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote && player.isSneaking()) {
            TileCrystalLens lens = MiscUtils.getTileAt(world, x, y, z, TileCrystalLens.class);
            if (lens != null && lens.getLensColor() != null) {
                ItemStack drop = lens.getLensColor()
                    .asStack();
                ItemUtils.dropItemNaturally(world, x + 0.5, y + 0.5, z + 0.5, drop);
                // 1.7.10: Use playSoundEffect with sound name string
                world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    Sounds.clipSwitch.getSoundName()
                        .toString(),
                    0.8F,
                    1.5F);
                lens.setLensColor(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TileCrystalLens te = MiscUtils.getTileAt(world, x, y, z, TileCrystalLens.class);
        if (te == null) return;
        te.onPlace(CrystalProperties.getCrystalProperties(stack));
    }

    // 1.7.10 compatibility methods
    public List<Integer> getValidMetas() {
        return Arrays.asList(0, 1, 2, 3, 4, 5);
    }

    @Override
    public String getStateName(int meta) {
        return "lens_" + meta;
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all lens directions are the same block with different metadata
        // Return the block itself once for each direction (6 directions)
        for (Integer meta : getValidMetas()) {
            ret.add(this);
        }
        return ret;
    }
}
