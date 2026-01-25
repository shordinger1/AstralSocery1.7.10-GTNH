/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 19:58
 */
public class BlockChalice extends BlockContainer {

    // Metadata values: 0 = ACTIVE (bottom), 1 = INACTIVE (top)
    public static final int META_ACTIVE = 0;
    public static final int META_INACTIVE = 1;

    public BlockChalice() {
        super(Material.iron);
        setHardness(2.0F);
        setStepSound(Block.soundTypeMetal);
        setResistance(15.0F);
        setHarvestLevel("pickaxe", 1);
        setLightLevel(0.3F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        int meta = worldIn.getBlockMetadata(x, y, z);

        // If this is the top part (inactive), delegate to bottom part
        if (meta != META_ACTIVE) {
            int newY = y - 1;
            Block blockBelow = worldIn.getBlock(x, newY, z);
            if (blockBelow == this && worldIn.getBlockMetadata(x, newY, z) == META_ACTIVE) {
                return blockBelow.onBlockActivated(worldIn, x, newY, z, playerIn, side, hitX, hitY, hitZ);
            }
            return false;
        }

        ItemStack interact = playerIn.getCurrentEquippedItem();
        TileChalice tc = MiscUtils.getTileAt(worldIn, x, y, z, TileChalice.class);
        if (tc != null && interact != null) {
            // Try IFluidContainerItem first
            if (interact.getItem() instanceof IFluidContainerItem fhi) {
                FluidStack st = fhi.getFluid(interact);
                if (st != null && st.amount > 0) {
                    // Try to empty container into tank
                    if (!worldIn.isRemote) {
                        FluidStack drained = fhi.drain(interact, 1000, true);
                        if (drained != null && tc.getTank()
                            .fill(drained, false) == drained.amount) {
                            tc.getTank()
                                .fill(drained, true);
                            if (!playerIn.capabilities.isCreativeMode) {
                                interact.stackSize--;
                                if (interact.stackSize <= 0) {
                                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                }
                            }
                        }
                        tc.markForUpdate();
                    }
                } else {
                    // Try to fill container from tank
                    if (!worldIn.isRemote) {
                        FluidStack toDrain = tc.getTank()
                            .drain(1000, false);
                        if (toDrain != null) {
                            int filled = fhi.fill(interact, toDrain, true);
                            if (filled > 0) {
                                tc.getTank()
                                    .drain(filled, true);
                                if (!playerIn.capabilities.isCreativeMode) {
                                    interact.stackSize--;
                                    if (interact.stackSize <= 0) {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                    }
                                }
                            }
                        }
                        tc.markForUpdate();
                    }
                }
                return true;
            }

            // Try FluidContainerRegistry for simple containers (buckets, bottles)
            FluidStack fluidInItem = FluidContainerRegistry.getFluidForFilledItem(interact);
            if (fluidInItem != null) {
                // Empty container into tank
                if (!worldIn.isRemote && tc.getTank()
                    .fill(fluidInItem, false) == fluidInItem.amount) {
                    tc.getTank()
                        .fill(fluidInItem, true);
                    ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(interact);
                    if (!playerIn.capabilities.isCreativeMode) {
                        interact.stackSize--;
                        if (interact.stackSize <= 0) {
                            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = emptyContainer;
                        } else if (emptyContainer != null) {
                            playerIn.inventory.addItemStackToInventory(emptyContainer);
                        }
                    }
                    tc.markForUpdate();
                }
                return true;
            } else if (FluidContainerRegistry.isEmptyContainer(interact)) {
                // Fill container from tank
                if (!worldIn.isRemote) {
                    FluidStack toDrain = tc.getTank()
                        .drain(FluidContainerRegistry.BUCKET_VOLUME, false);
                    if (toDrain != null && toDrain.amount == FluidContainerRegistry.BUCKET_VOLUME) {
                        ItemStack filled = FluidContainerRegistry.fillFluidContainer(toDrain, interact);
                        if (filled != null) {
                            tc.getTank()
                                .drain(FluidContainerRegistry.BUCKET_VOLUME, true);
                            if (!playerIn.capabilities.isCreativeMode) {
                                interact.stackSize--;
                                if (interact.stackSize <= 0) {
                                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = filled;
                                } else if (!playerIn.inventory.addItemStackToInventory(filled)) {
                                    playerIn.dropPlayerItemWithRandomChoice(filled, false);
                                }
                            }
                            tc.markForUpdate();
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        if (meta == META_ACTIVE) {
            // Bottom part - tall
            this.setBlockBounds(2F / 16F, 0F, 2F / 16F, 14F / 16F, 2F, 14F / 16F);
        } else {
            // Top part - below block
            this.setBlockBounds(2F / 16F, -1F, 2F / 16F, 14F / 16F, 1F, 14F / 16F);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        if (meta == META_ACTIVE) {
            return AxisAlignedBB.getBoundingBox(x + 2D / 16D, y, z + 2D / 16D, x + 14D / 16D, y + 2, z + 14D / 16D);
        } else {
            return AxisAlignedBB.getBoundingBox(x + 2D / 16D, y - 1, z + 2D / 16D, x + 14D / 16D, y + 1, z + 14D / 16D);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Place the top part
        worldIn.setBlock(x.posX, x.posY, x.posZ, y + 1, z, BlocksAS.blockChalice, META_INACTIVE, 3);
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return super.canPlaceBlockAt(worldIn, x, y, z) && y < 255 && super.canPlaceBlockAt(worldIn, x, y + 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == META_ACTIVE) {
            // Bottom part - check if top part exists
            if (world.isAirBlock(x, y + 1, z)) {
                world.setBlockToAir(x, y, z);
            }
        } else {
            // Top part - check if bottom part exists
            if (world.isAirBlock(x, y - 1, z)) {
                world.setBlockToAir(x, y, z);
            }
        }
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        TileChalice tw = MiscUtils.getTileAt(worldIn, x, y, z, TileChalice.class);
        if (tw != null) {
            return WrapMathHelper.ceil(tw.getPercFilled() * 15F);
        }
        return 0;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == META_ACTIVE) {
            return new TileChalice();
        }
        return null;
    }

    @Override
    public int damageDropped(int meta) {
        return META_ACTIVE;
    }
}
