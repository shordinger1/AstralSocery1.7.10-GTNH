/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileInventoryBase;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementRelay
 * Created by HellFirePvP
 * Date: 30.11.2016 / 13:16
 */
public class BlockAttunementRelay extends BlockContainer {

    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(3F / 16F, 0, 3F / 16F, 13F / 16F, 3F / 16F, 13F / 16F);

    public BlockAttunementRelay() {
        super(Material.glass);
        setHardness(0.5F);
        setHarvestLevel("pickaxe", 0);
        setResistance(1.0F);
        setLightLevel(0.25F);
        setStepSound(Block.soundTypeGlass);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAttunementRelay();
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        if (!worldIn.isRemote) {
            TileEntity inv = MiscUtils.getTileAt(worldIn, x, y, z, TileEntity.class);
            if (inv != null) {
                // In 1.7.10, check if TileEntity implements IInventory
                if (inv instanceof IInventory) {
                    IItemHandler handle = new InvWrapper((IInventory) inv);
                    ItemUtils.dropInventory(handle, worldIn, new BlockPos(x, y, z));
                } else if (inv instanceof ISidedInventory) {
                    IItemHandler handle = new InvWrapper((ISidedInventory) inv);
                    ItemUtils.dropInventory(handle, worldIn, new BlockPos(x, y, z));
                }
            }

            BlockAltar.startSearchForRelayUpdate(worldIn, x, y, z);
        }

        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        startSearchRelayLinkThreadAt(worldIn, pos, true);
    }

    public static void startSearchRelayLinkThreadAt(final World world, final BlockPos pos, final boolean recUpdate) {
        Thread searchThread = new Thread(new Runnable() {

            @Override
            public void run() {
                BlockPos closestAltar = null;
                double dstSqOtherRelay = Double.MAX_VALUE;
                BlockArray relaysAndAltars = BlockDiscoverer
                    .searchForBlocksAround(world, pos, 16, new BlockStateCheck.Block(BlocksAS.blockAltar));
                for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : relaysAndAltars.getPattern()
                    .entrySet()) {
                    if (entry.getValue().type.equals(BlocksAS.blockAltar)) {
                        double dist = distanceSq(pos, entry.getKey());
                        if (closestAltar == null || dist < distanceSq(pos, closestAltar)) {
                            closestAltar = entry.getKey();
                        }
                    } else {
                        double dstSqOther = distanceSq(entry.getKey(), pos);
                        if (dstSqOther < dstSqOtherRelay) {
                            dstSqOtherRelay = dstSqOther;
                        }
                    }
                }

                final BlockPos finalClosestAltar = closestAltar;
                final double finalDstSqOtherRelay = dstSqOtherRelay;
                final int px = pos.getX(), py = pos.getY(), pz = pos.getZ();
                AstralSorcery.proxy.scheduleDelayed(new Runnable() {

                    @Override
                    public void run() {
                        TileAttunementRelay tar = MiscUtils.getTileAt(world, px, py, pz, TileAttunementRelay.class);
                        if (tar != null) {
                            tar.updatePositionData(finalClosestAltar, finalDstSqOtherRelay);
                        }
                        if (recUpdate) {
                            BlockAltar.startSearchForRelayUpdate(world, px, py, pz);
                        }
                    }
                });
            }

            private double distanceSq(BlockPos p1, BlockPos p2) {
                double dx = p1.getX() - p2.getX();
                double dy = p1.getY() - p2.getY();
                double dz = p1.getZ() - p2.getZ();
                return dx * dx + dy * dy + dz * dz;
            }
        });
        searchThread.setName("AttRelay PositionFinder at " + pos.toString());
        searchThread.start();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getCurrentEquippedItem();
            if (held != null && held.stackSize > 0) {
                TileAttunementRelay tar = MiscUtils.getTileAt(worldIn, x, y, z, TileAttunementRelay.class);
                if (tar != null) {
                    TileInventoryBase.ItemHandlerTile mod = tar.getInventoryHandler();
                    ItemStack slotStack = mod.getStackInSlot(0);
                    if (slotStack != null && slotStack.stackSize > 0) {
                        playerIn.inventory.addItemStackToInventory(slotStack);
                        mod.setStackInSlot(0, null);
                        tar.markForUpdate();
                    }

                    if (!worldIn.isAirBlock(x, y + 1, z)) {
                        return false;
                    }

                    ItemStack toSet = held.copy();
                    toSet.stackSize = 1;
                    mod.setStackInSlot(0, toSet);
                    // 1.7.10: Use playSoundEffect with sound string
                    worldIn.playSoundEffect(
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        "step.stone",
                        0.2F,
                        ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    if (!playerIn.capabilities.isCreativeMode) {
                        held.stackSize--;
                    }
                    tar.markForUpdate();
                }
            } else {
                TileAttunementRelay tar = MiscUtils.getTileAt(worldIn, x, y, z, TileAttunementRelay.class);
                if (tar != null) {
                    TileInventoryBase.ItemHandlerTile mod = tar.getInventoryHandler();
                    ItemStack slotStack = mod.getStackInSlot(0);
                    if (slotStack != null && slotStack.stackSize > 0) {
                        ItemStack stack = slotStack.copy();
                        playerIn.inventory.addItemStackToInventory(stack);
                        mod.setStackInSlot(0, null);
                        tar.markForUpdate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(3F / 16F, 0F, 3F / 16F, 13F / 16F, 3F / 16F, 13F / 16F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + 3D / 16D, y, z + 3D / 16D, x + 13D / 16D, y + 3D / 16D, z + 13D / 16D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + 3D / 16D, y, z + 3D / 16D, x + 13D / 16D, y + 3D / 16D, z + 13D / 16D);
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
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

}
