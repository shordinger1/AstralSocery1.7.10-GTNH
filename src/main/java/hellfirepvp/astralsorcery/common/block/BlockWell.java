/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Well - Starlight well block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.FluidHelper;
import hellfirepvp.astralsorcery.common.util.WellLiquefaction;

/**
 * BlockWell - Starlight well (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Starlight well for liquid collection</li>
 * <li>Has TileEntity (TileWell)</li>
 * <li>Part of liquid starlight system</li>
 * <li>GUI for liquid management</li>
 * </ul>
 */
public class BlockWell extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconWell;

    public BlockWell() {
        super(Material.rock);
        setHardness(2.5F);
        setResistance(15.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // Set block bounds (similar to cauldron - hollow container)
        // Well is approximately full block height but hollow inside
        // Values are 0-16 (block coordinates)
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Well is hollow
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false; // Custom rendering
    }

    /**
     * Get render type - use TESR for custom rendering
     */
    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    /**
     * Add collision boxes for well shape
     * <p>
     * Well has base (5 pixels high) and 4 walls (11 pixels high)
     * Players can step into the well
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB entityBox,
        List<AxisAlignedBB> collidingBoxes, Entity entity) {
        // Well base (bottom section) - 5 pixels high
        AxisAlignedBB baseBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 0D,
            (double) z + 1D / 16D,
            (double) x + 15D / 16D,
            (double) y + 5D / 16D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(baseBB)) {
            collidingBoxes.add(baseBB);
        }

        // Well walls (top section) - 4 walls, 11 pixels high
        AxisAlignedBB northBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 5D / 16D,
            (double) z + 1D / 16D,
            (double) x + 2D / 16D,
            (double) y + 1D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(northBB)) {
            collidingBoxes.add(northBB);
        }

        AxisAlignedBB westBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 5D / 16D,
            (double) z + 1D / 16D,
            (double) x + 15D / 16D,
            (double) y + 1D,
            (double) z + 2D / 16D);
        if (entityBox.intersectsWith(westBB)) {
            collidingBoxes.add(westBB);
        }

        AxisAlignedBB southBB = AxisAlignedBB.getBoundingBox(
            (double) x + 14D / 16D,
            (double) y + 5D / 16D,
            (double) z + 1D / 16D,
            (double) x + 15D / 16D,
            (double) y + 1D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(southBB)) {
            collidingBoxes.add(southBB);
        }

        AxisAlignedBB eastBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 5D / 16D,
            (double) z + 14D / 16D,
            (double) x + 15D / 16D,
            (double) y + 1D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(eastBB)) {
            collidingBoxes.add(eastBB);
        }
    }

    /**
     * Comparator output - based on liquid starlight fill level
     * Returns 0-15 redstone signal based on well fill percentage
     */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileWell well = (TileWell) world.getTileEntity(x, y, z);
        if (well != null) {
            // Get fill percentage from TileWell
            // In 1.7.10 simplified version: check if well has liquid
            // TODO: When TileWell fluid system is implemented:
            // float percentage = well.getPercFilled();
            // return MathHelper.ceiling_float_int(percentage * 15F);

            // Simplified version for now
            // Return signal based on whether well has any fluid
            return well.getHeldFluid() != null ? 15 : 0;
        }
        return 0;
    }

    /**
     * Has comparator output
     */
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            TileWell well = (TileWell) world.getTileEntity(x, y, z);
            if (well == null) {
                return false;
            }

            ItemStack heldItem = player.getCurrentEquippedItem();

            if (heldItem != null && heldItem.stackSize > 0) {
                // Try to insert catalyst (liquefaction item)
                WellLiquefaction.LiquefactionEntry entry = WellLiquefaction.getLiquefactionEntry(heldItem);

                if (entry != null) {
                    // Check if slot 0 is empty
                    ItemStack currentCatalyst = well.getStackInSlot(0);

                    if (currentCatalyst != null && currentCatalyst.stackSize > 0) {
                        return false; // Already has a catalyst
                    }

                    // Check if block above is air
                    boolean isAirAbove = world.isAirBlock(x, y + 1, z);

                    if (!isAirAbove) {
                        return false;
                    }

                    // Insert the catalyst
                    well.setInventorySlotContents(0, heldItem.splitStack(1));
                    world.playSoundAtEntity(
                        player,
                        "random.pop",
                        0.2F,
                        ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    world.markBlockForUpdate(x, y, z);
                    return true;
                }

                // Try to fill fluid container from well
                Fluid heldFluid = FluidHelper.getFluidType(heldItem);

                if (heldFluid != null) {
                    // Item is already filled, don't fill it
                    return false;
                }

                // Try to fill empty container
                Fluid wellFluid = well.getHeldFluid();
                int fluidAmount = well.getFluidAmount();

                if (wellFluid != null && fluidAmount > 0) {
                    // Try IFluidContainerItem first
                    if (heldItem.getItem() instanceof IFluidContainerItem) {
                        IFluidContainerItem container = (IFluidContainerItem) heldItem.getItem();

                        // Try to fill without actually doing it
                        ItemStack copy = heldItem.copy();
                        copy.stackSize = 1;
                        int canFill = container
                            .fill(copy, new FluidStack(wellFluid, FluidContainerRegistry.BUCKET_VOLUME), false);

                        if (canFill > 0) {
                            // Actually fill the item
                            int filled = container.fill(
                                heldItem,
                                new FluidStack(wellFluid, Math.min(canFill, well.getFluidAmount())),
                                true);

                            if (filled > 0) {
                                // Drain from well
                                well.drain(net.minecraftforge.common.util.ForgeDirection.UNKNOWN, filled, true);
                                world.playSoundAtEntity(player, "liquid.fill", 1F, 1F);
                                world.markBlockForUpdate(x, y, z);
                                return true;
                            }
                        }
                    } else {
                        // Try FluidContainerRegistry
                        FluidStack fluidStack = new FluidStack(
                            wellFluid,
                            Math.min(FluidContainerRegistry.BUCKET_VOLUME, well.getFluidAmount()));
                        ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluidStack, heldItem);

                        if (filledContainer != null) {
                            // Get the amount that was actually filled
                            FluidStack filledFluid = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
                            if (filledFluid != null && filledFluid.amount > 0) {
                                // Drain from well
                                well.drain(
                                    net.minecraftforge.common.util.ForgeDirection.UNKNOWN,
                                    filledFluid.amount,
                                    true);

                                // Replace held item with filled container
                                if (!player.capabilities.isCreativeMode) {
                                    heldItem.stackSize--;
                                    if (heldItem.stackSize <= 0) {
                                        player.inventory.mainInventory[player.inventory.currentItem] = filledContainer;
                                    } else {
                                        // Add to inventory or drop
                                        if (!player.inventory.addItemStackToInventory(filledContainer)) {
                                            player.dropPlayerItemWithRandomChoice(filledContainer, false);
                                        }
                                    }
                                }
                                world.playSoundAtEntity(player, "liquid.fill", 1F, 1F);
                                world.markBlockForUpdate(x, y, z);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileWell well = (TileWell) world.getTileEntity(x, y, z);
        if (well != null && !world.isRemote) {
            ItemStack catalyst = well.getStackInSlot(0);
            if (catalyst != null && catalyst.stackSize > 0) {
                well.breakCatalyst();
            }
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    public int quantityDropped(Random rand) {
        return 1;

    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, 0));
        return drops;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWell();

        // TODO: Implement well system
        // - Liquid starlight collection
        // - GUI system
        // - Liquid management
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // For TESR blocks, we need a simple icon for the item in hand/inventory
        // Register all block textures and store reference for quick access
        hellfirepvp.astralsorcery.client.util.BlockTextureRegister.registerAll(reg);
        iconWell = reg.registerIcon("astralsorcery:blocks/well");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconWell;
    }
}
