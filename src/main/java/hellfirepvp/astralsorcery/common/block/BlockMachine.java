/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Machine Block - Multi-type machine container
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Machine Block
 * <p>
 * A multi-type machine block that contains various machines.
 * <p>
 * Machine Types (metadata 0-1):
 * - 0: TELESCOPE - Star viewing device
 * - 1: GRINDSTONE - Crystal grinding device
 * <p>
 * Features:
 * - Multiple machine types in one block
 * - Each type has its own TileEntity
 * - Custom harvest tools per type
 * - Special block break effects
 * <p>
 * Uses:
 * - Telescope: View stars at any time (opens GUI)
 * - Grindstone: Grind items into dust (right-click interaction)
 * <p>
 * 1.7.10 API Notes:
 * - Removed: PlayerInteractEvent.RightClickBlock event (1.8+ feature)
 *   Grindstone interaction now handled in onBlockActivated()
 * - Removed: EnumHand parameter (off-hand is 1.9+ feature)
 *   Only main hand is considered in 1.7.10
 * - Removed: IBlockState (1.8+ feature)
 *   Uses metadata directly instead
 */
public class BlockMachine extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconTelescope;
    @SideOnly(Side.CLIENT)
    private IIcon iconGrindstone;

    public static final int META_TELESCOPE = 0;
    public static final int META_GRINDSTONE = 1;

    public BlockMachine() {
        super(Material.rock);

        setHardness(3.0F);
        setResistance(25.0F);
        setStepSound(soundTypeStone);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Not a full block
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false; // Special rendering
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconTelescope = IconHelper.registerIcon(register, "blocktelescope");
        iconGrindstone = IconHelper.registerIcon(register, "blockgrindstone");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (meta) {
            case META_TELESCOPE:
                return iconTelescope;
            case META_GRINDSTONE:
                return iconGrindstone;
            default:
                return iconTelescope;
        }
    }

    public int damageDropped(int meta) {
        return meta; // Drop with same metadata
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Add all machine types to creative tab
        list.add(new ItemStack(item, 1, META_TELESCOPE));
        list.add(new ItemStack(item, 1, META_GRINDSTONE));
    }

    public String getHarvestTool(int metadata) {
        switch (metadata) {
            case META_TELESCOPE:
                return "axe"; // Telescope harvested with axe
            case META_GRINDSTONE:
                return "pickaxe"; // Grindstone harvested with pickaxe
            default:
                return "pickaxe";
        }
    }

    public int getHarvestLevel(int metadata) {
        return 1; // Stone tier
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Set metadata from stack
        int meta = stack.getItemDamage();
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);

        // 1.7.10: Removed BlockStructural placement above telescope
        // In 1.12.2, telescope placed a structural block above it
        // In 1.7.10, we skip this to simplify the implementation
    }

    /**
     * Break block - drop grindstone item
     * 1.7.10: Removed IBlockState parameter (uses metadata directly)
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block blockBroken, int meta) {
        // Drop grindstone item if present
        if (meta == META_GRINDSTONE) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof hellfirepvp.astralsorcery.common.tile.TileGrindstone) {
                hellfirepvp.astralsorcery.common.tile.TileGrindstone tgr =
                    (hellfirepvp.astralsorcery.common.tile.TileGrindstone) te;
                ItemStack grind = tgr.getGrindingItem();

                if (grind != null && grind.stackSize > 0) {
                    // Drop item
                    dropBlockAsItem(world, x, y, z, grind);
                }
            }
        }

        super.breakBlock(world, x, y, z, blockBroken, meta);
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Open GUI based on machine type
        int meta = world.getBlockMetadata(x, y, z);

        if (meta == META_TELESCOPE) {
            // Open telescope GUI using ModularUI
            if (world.isRemote) {
                return true;
            }
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof hellfirepvp.astralsorcery.common.tile.TileTelescope) {
                TileEntityGuiFactory.INSTANCE.open(player, te);
            }
            return true;
        } else if (meta == META_GRINDSTONE) {
            // Grindstone interaction
            // 1.7.10: Removed EnumHand (off-hand is 1.9+ feature)
            // Only main hand interaction is supported
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof hellfirepvp.astralsorcery.common.tile.TileGrindstone)) {
                return false;
            }

            hellfirepvp.astralsorcery.common.tile.TileGrindstone tgr =
                (hellfirepvp.astralsorcery.common.tile.TileGrindstone) te;

            if (!world.isRemote) {
                // Server-side logic
                ItemStack grind = tgr.getGrindingItem();

                if (grind != null && grind.stackSize > 0) {
                    // Item is currently in grindstone
                    if (player.isSneaking()) {
                        // Sneak + Right-click: Remove item
                        player.inventory.mainInventory[player.inventory.currentItem] = grind;
                        tgr.setGrindingItem(null);
                        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.pop", 0.5F, 1.0F);
                    } else {
                        // Right-click: Try to grind
                        hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe.GrindResult result =
                            tgr.tryGrind();

                        if (result != null) {
                            switch (result.getType()) {
                                case SUCCESS:
                                    // Update item
                                    tgr.markDirty();
                                    break;
                                case ITEMCHANGE:
                                    // Change to new item
                                    tgr.setGrindingItem(result.getStack());
                                    break;
                                case FAIL_BREAK_ITEM:
                                    // Item broke
                                    tgr.setGrindingItem(null);
                                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 0.5F, 1.0F);
                                    break;
                                case FAIL_SILENT:
                                default:
                                    // Do nothing
                                    break;
                            }
                            tgr.playWheelEffect();
                        }
                    }
                } else {
                    // No item in grindstone
                    ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];

                    if (held != null && held.stackSize > 0) {
                        hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe recipe =
                            hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry.findMatchingRecipe(held);

                        if (recipe != null || hellfirepvp.astralsorcery.common.tile.TileGrindstone.SwordSharpenHelper.canBeSharpened(held)) {
                            // Place item in grindstone
                            ItemStack toSet = held.copy();
                            toSet.stackSize = 1;
                            tgr.setGrindingItem(toSet);
                            held.stackSize--;

                            if (held.stackSize <= 0) {
                                player.inventory.mainInventory[player.inventory.currentItem] = null;
                            }

                            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.pop", 0.5F, 1.0F);
                        } else if (player.isSneaking()) {
                            // Sneak + Right-click with invalid item: Pass through
                            return false;
                        }
                    }
                }
            } else {
                // Client-side: Play particles if grinding
                ItemStack grind = tgr.getGrindingItem();
                if (grind != null && grind.stackSize > 0) {
                    hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe recipe =
                        hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry.findMatchingRecipe(grind);

                    if (recipe != null || hellfirepvp.astralsorcery.common.tile.TileGrindstone.SwordSharpenHelper.canBeSharpened(grind)) {
                        // Spawn critical particles
                        for (int j = 0; j < 8; j++) {
                            world.spawnParticle("crit",
                                x + 0.5 + (world.rand.nextBoolean() ? 1 : -1) * world.rand.nextFloat() * 0.3,
                                y + 0.8 + (world.rand.nextBoolean() ? 1 : -1) * world.rand.nextFloat() * 0.3,
                                z + 0.4 + (world.rand.nextBoolean() ? 1 : -1) * world.rand.nextFloat() * 0.3,
                                0, 0, 0);
                        }
                    }
                }
            }

            return true;
        }

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        switch (meta) {
            case META_GRINDSTONE:
                return new hellfirepvp.astralsorcery.common.tile.TileGrindstone();
            case META_TELESCOPE:
                return new hellfirepvp.astralsorcery.common.tile.TileTelescope();
            default:
                return new hellfirepvp.astralsorcery.common.tile.TileGrindstone();
        }
    }

    public boolean hasTileEntity(int metadata) {
        return true;

    }

    /**
     * Get machine name for display
     */
    public static String getMachineName(int meta) {
        switch (meta) {
            case META_TELESCOPE:
                return "Telescope";
            case META_GRINDSTONE:
                return "Grindstone";
            default:
                return "Unknown";
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // TODO: Add particle effects for each machine type
        // switch (world.getBlockMetadata(x, y, z)) {
        // case META_TELESCOPE:
        // // Star particles
        // break;
        // case META_GRINDSTONE:
        // // Dust particles
        // break;
        // }

        /**
         * NOTE: Machine Type System
         * <p>
         * Original version:
         * - Uses PropertyEnum<MachineType>
         * - MachineType enum implements IVariantTileProvider
         * - Each type provides its own TileEntity
         * - Uses lambda functions for TileEntity creation
         * <p>
         * In 1.7.10:
         * - Uses metadata (0-1) for machine types
         * - Manual switch statements for type-specific logic
         * - TileEntity creation based on metadata
         * - No PropertyEnum (1.8+ feature)
         */

        /**
         * NOTE: TileEntity Implementation
         * <p>
         * Original version:
         * - TileTelescope: View stars, track constellations
         * - TileGrindstone: Grind items, recipes, inventory
         * - Both have custom GUIs
         * - Both have special rendering
         * <p>
         * In 1.7.10:
         * - TODO: Implement TileTelescope
         * - TODO: Implement TileGrindstone
         * - TODO: Implement GUI system (1.7.10 GuiScreen)
         * - TODO: Implement container system
         * - TODO: Implement recipe system for grindstone
         */
    }
}
