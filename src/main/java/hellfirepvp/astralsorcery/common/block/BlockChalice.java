/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Chalice block - Liquid storage container (2-block tall)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * BlockChalice - Liquid storage chalice (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>2-block tall structure (ACTIVE=true is top, false is bottom)</li>
 * <li>24000mb liquid capacity</li>
 * <li>Comparator output based on fill level</li>
 * <li>Redstone controllable</li>
 * <li>BlockContainer with TileEntity</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Metadata: 0=ACTIVE (top), 1=INACTIVE (bottom)</li>
 * <li>Only ACTIVE (top) has TileEntity</li>
 * <li>Collision box: 2 blocks tall</li>
 * <li>No IBlockState - uses metadata directly</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had:
 * <ul>
 * <li>PropertyBool ACTIVE - 1.8+ property</li>
 * <li>onBlockActivated() - liquid container interaction</li>
 * <li>getComparatorInputOverride() - 0-15 signal based on fill %</li>
 * <li>getBoundingBox() - spans 2 blocks</li>
 * <li>TileChalice with liquid storage</li>
 * </ul>
 */
public class BlockChalice extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconChalice;

    /**
     * Constructor
     */
    public BlockChalice() {
        super(Material.iron);

        setHardness(2.0F);
        setResistance(15.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 1); // Stone tier
        setLightLevel(0.3F);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.GOLD
        // Using default iron color
    }

    // @Override
    // public String getUnlocalizedName() {
    // return "tile.blockchalice.name";
    // }

    /**
     * Is this a full block?
     */
    public boolean isOpaqueCube() {
        return false;

    }

    /**
     * Does this block render normally?
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Get render type - use TESR for custom rendering
     */
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    /**
     * Comparator output - based on fluid level
     * Returns 0-15 redstone signal based on tank fill percentage
     */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        // Get TileChalice from bottom block (metadata=1)
        int meta = world.getBlockMetadata(x, y, z);

        // Only bottom block (meta=1) has TileEntity
        // If this is top block (meta=0), get the TileEntity from block above
        int tileX = x;
        int tileY = (meta == 0) ? y + 1 : y;
        int tileZ = z;

        hellfirepvp.astralsorcery.common.tile.TileChalice tile = (hellfirepvp.astralsorcery.common.tile.TileChalice) world
            .getTileEntity(tileX, tileY, tileZ);

        if (tile != null && side >= 0 && side <= 5) {
            // Get fill percentage from TileChalice fluid tank
            float percentage = tile.getPercFilled();
            // Return 0-15 signal based on fill percentage
            return MathHelper.ceiling_float_int(percentage * 15F);
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

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // For TESR blocks, we need a simple icon for the item in hand/inventory
        iconChalice = reg.registerIcon("astralsorcery:blocks/chalice");
        LogHelper.info("[BlockChalice] Registered icon: " + iconChalice.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconChalice;
    }

    /**
     * On block activated - handle liquid interaction
     * <p>
     * Allows players to fill/empty fluid containers (buckets, etc.)
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Get TileChalice from bottom block (metadata=1)
        int meta = world.getBlockMetadata(x, y, z);

        // Only bottom block (meta=1) has TileEntity with fluid tank
        // If this is top block (meta=0), get the TileEntity from block above
        int tileX = x;
        int tileY = (meta == 0) ? y + 1 : y;
        int tileZ = z;

        hellfirepvp.astralsorcery.common.tile.TileChalice chalice = (hellfirepvp.astralsorcery.common.tile.TileChalice) world
            .getTileEntity(tileX, tileY, tileZ);

        if (chalice == null) {
            return false;
        }

        // Get held item
        net.minecraft.item.ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) {
            return false;
        }

        // Try to interact with fluid container using 1.7.10 FluidContainerRegistry
        net.minecraftforge.fluids.FluidStack fluidInChalice = chalice
            .getTankInfo(net.minecraftforge.common.util.ForgeDirection.UNKNOWN)[0].fluid;

        // Try to fill container from chalice
        net.minecraftforge.fluids.FluidStack drained = chalice.drain(
            net.minecraftforge.common.util.ForgeDirection.UNKNOWN,
            net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME,
            false);
        if (drained != null && drained.amount > 0) {
            // Try to fill player's container
            net.minecraft.item.ItemStack filledContainer = net.minecraftforge.fluids.FluidContainerRegistry
                .fillFluidContainer(drained, heldItem);

            if (filledContainer != null) {
                // Actually drain from chalice
                chalice.drain(
                    net.minecraftforge.common.util.ForgeDirection.UNKNOWN,
                    net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME,
                    true);

                // Update player's held item
                if (!player.capabilities.isCreativeMode) {
                    if (heldItem.stackSize == 1) {
                        player.inventory.mainInventory[player.inventory.currentItem] = filledContainer;
                    } else {
                        heldItem.stackSize--;
                        if (heldItem.stackSize <= 0) {
                            player.inventory.mainInventory[player.inventory.currentItem] = null;
                        }
                        // Add filled container to inventory
                        if (!player.inventory.addItemStackToInventory(filledContainer)) {
                            // Drop if inventory full
                            net.minecraft.entity.item.EntityItem entityItem = new net.minecraft.entity.item.EntityItem(
                                world,
                                x + 0.5,
                                y + 0.5,
                                z + 0.5,
                                filledContainer);
                            world.spawnEntityInWorld(entityItem);
                        }
                    }
                }

                world.markBlockForUpdate(tileX, tileY, tileZ);
                hellfirepvp.astralsorcery.common.util.LogHelper
                    .debug("[BlockChalice] Filled container from chalice at " + tileX + "," + tileY + "," + tileZ);
                return true;
            }
        }

        // Try to empty container into chalice
        net.minecraftforge.fluids.FluidStack fluidInContainer = net.minecraftforge.fluids.FluidContainerRegistry
            .getFluidForFilledItem(heldItem);
        if (fluidInContainer != null) {
            // Try to fill chalice
            int filled = chalice.fill(net.minecraftforge.common.util.ForgeDirection.UNKNOWN, fluidInContainer, false);
            if (filled > 0) {
                // Actually fill chalice
                chalice.fill(net.minecraftforge.common.util.ForgeDirection.UNKNOWN, fluidInContainer, true);

                // Update player's held item (empty container)
                if (!player.capabilities.isCreativeMode) {
                    net.minecraft.item.ItemStack emptyContainer = net.minecraftforge.fluids.FluidContainerRegistry
                        .drainFluidContainer(heldItem);
                    if (heldItem.stackSize == 1) {
                        player.inventory.mainInventory[player.inventory.currentItem] = emptyContainer;
                    } else {
                        heldItem.stackSize--;
                        if (heldItem.stackSize <= 0) {
                            player.inventory.mainInventory[player.inventory.currentItem] = null;
                        }
                        // Add empty container to inventory
                        if (emptyContainer != null && !player.inventory.addItemStackToInventory(emptyContainer)) {
                            // Drop if inventory full
                            net.minecraft.entity.item.EntityItem entityItem = new net.minecraft.entity.item.EntityItem(
                                world,
                                x + 0.5,
                                y + 0.5,
                                z + 0.5,
                                emptyContainer);
                            world.spawnEntityInWorld(entityItem);
                        }
                    }
                }

                world.markBlockForUpdate(tileX, tileY, tileZ);
                hellfirepvp.astralsorcery.common.util.LogHelper
                    .debug("[BlockChalice] Emptied container into chalice at " + tileX + "," + tileY + "," + tileZ);
                return true;
            }
        }

        return false;

    }

    /**
     * On block placed - create 2-block structure
     * <p>
     * Places bottom block (metadata=1) above when top block (metadata=0) is placed.
     * Bottom block has TileEntity, top block does not.
     */
    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);

        // Get metadata of placed block
        int meta = worldIn.getBlockMetadata(x, y, z);

        // If placing top block (meta=0), place bottom block (meta=1) above
        if (meta == 0 && !worldIn.isRemote) {
            int yAbove = y + 1;

            // Check if space above is available
            if (worldIn.isAirBlock(x, yAbove, z)) {
                worldIn.setBlock(x, yAbove, z, this, 1, 3); // metadata=1 for bottom block (has TileEntity)

                // Log placement
                hellfirepvp.astralsorcery.common.util.LogHelper.info(
                    "[BlockChalice] Created 2-block structure at " + x
                        + ","
                        + y
                        + ","
                        + z
                        + " (bottom at y="
                        + yAbove
                        + ")");
            }
        }
    }

    /**
     * On neighbor change - check structure integrity
     * <p>
     * Breaks block if its partner is missing.
     * NOTE: Disabled due to infinite loop issue in 1.7.10
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);

        if (world.isRemote) return;

        // NOTE: Disabled self-break logic to prevent infinite destruction loop
        // The structure will still be created on placement, just won't auto-break
        // This is a 1.7.10 compatibility fix
        /*
        int meta = world.getBlockMetadata(x, y, z);

        // If this is the top block (meta=0), check if bottom block exists below
        if (meta == 0) {
            int yBelow = y - 1;
            if (world.getBlock(x, yBelow, z) != this || world.getBlockMetadata(x, yBelow, z) != 1) {
                // Bottom block missing, break self
                world.func_147480_a(x, y, z, true); // breakBlock + drop items
                hellfirepvp.astralsorcery.common.util.LogHelper
                    .info("[BlockChalice] Breaking top block at " + x + "," + y + "," + z + " - bottom block missing");
            }
        }
        // If this is the bottom block (meta=1), check if top block exists above
        else if (meta == 1) {
            int yAbove = y + 1;
            if (world.getBlock(x, yAbove, z) != this || world.getBlockMetadata(x, yAbove, z) != 0) {
                // Top block missing, break self
                world.func_147480_a(x, y, z, true);
                hellfirepvp.astralsorcery.common.util.LogHelper
                    .info("[BlockChalice] Breaking bottom block at " + x + "," + y + "," + z + " - top block missing");
            }
        }
        */
    }

    /**
     * Can place block at this position
     * <p>
     * Checks if there's space for the 2-block structure.
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // Check base conditions
        if (!super.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }

        // Check if space above is available (for the bottom block)
        int yAbove = y + 1;
        if (yAbove >= world.getHeight()) {
            return false; // Would go above world height
        }

        // Space above must be replaceable (air or replaceable block)
        Block blockAbove = world.getBlock(x, yAbove, z);
        if (!blockAbove.isReplaceable(world, x, yAbove, z)) {
            return false;
        }

        return true;
    }

    /**
     * Get the item dropped
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    /**
     * Quantity dropped
     */
    public int quantityDropped(Random rand) {
        return 1;

    }

    /**
     * Get drops
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, 0));
        return drops;

    }

    /**
     * Create new tile entity
     * <p>
     * Only bottom block (metadata=1) has TileEntity
     * Matches 1.12.2 behavior
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        // Bottom block (metadata=1) has TileEntity
        if (meta == 1) {
            return new hellfirepvp.astralsorcery.common.tile.TileChalice();
        }
        return null; // Top block doesn't need TileEntity

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ PropertyBool ACTIVE - 1.8+ property
        // - 1.7.10: metadata 0=top, 1=bottom
        //
        // ❌ getBoundingBox(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ signature
        // - ACTIVE=true: Y from 0 to 2
        // - ACTIVE=false: Y from -1 to 1
        // - 14x14 horizontal size
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
        //
        // ❌ createBlockState()
        // - 1.8+ method

        /**
         * NOTE: Liquid system
         * <p>
         * Original version integrates with:
         * - TileChalice - stores 24000mb liquid
         * - Forge fluid system - liquid container interaction
         * - Comparator output - signal 0-15 based on fill level
         * - Redstone control - can disable interaction
         * <p>
         * In 1.7.10 simplified version:
         * - TODO: Implement TileChalice with fluid storage
         * - TODO: Implement fluid container item interaction
         * - TODO: Implement comparator output
         * - TODO: Implement redstone control
         */
    }

    /**
     * Phase 2.3: Get moon phase at block position
     * 1.7.10: Simplified moon phase calculation
     *
     * @param world The world
     * @param x     Block X coordinate
     * @param y     Block Y coordinate
     * @param z     Block Z coordinate
     * @return Moon phase (0-7, where 0=Full Moon, 4=New Moon)
     */
    public static int getMoonPhaseAt(World world, int x, int y, int z) {
        long time = world.getTotalWorldTime();
        // Minecraft moon phase cycle: 8 phases, each lasting 1 in-game day (24000 ticks)
        // Phase 0 = Full Moon, Phase 4 = New Moon
        long dayTime = time % 24000L;
        long day = time / 24000L;
        // Offset calculation to match 1.7.10 moon phase
        int phase = (int) ((day + 4) % 8);
        return phase;
    }

    /**
     * Phase 2.3: Check if moon phase matches specified phase
     * Useful for altar interactions that require specific moon phases
     *
     * @param world The world
     * @param x     Block X coordinate
     * @param y     Block Y coordinate
     * @param z     Block Z coordinate
     * @param phase The moon phase to check (0-7)
     * @return true if moon phase matches
     */
    public static boolean isMoonPhase(World world, int x, int y, int z, int phase) {
        return getMoonPhaseAt(world, x, y, z) == phase;
    }

    /**
     * Phase 2.3: Get moon phase name
     *
     * @param phase Moon phase number (0-7)
     * @return Moon phase name
     */
    public static String getMoonPhaseName(int phase) {
        switch (phase % 8) {
            case 0:
                return "Full Moon";
            case 1:
                return "Waning Gibbous";
            case 2:
                return "Last Quarter";
            case 3:
                return "Waning Crescent";
            case 4:
                return "New Moon";
            case 5:
                return "Waxing Crescent";
            case 6:
                return "First Quarter";
            case 7:
                return "Waxing Gibbous";
            default:
                return "Unknown";
        }
    }
}
