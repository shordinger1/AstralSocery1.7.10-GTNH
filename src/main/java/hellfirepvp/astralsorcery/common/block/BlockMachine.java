/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Machine Block - Multi-type machine container
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

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
 * - Telescope: View stars at any time
 * - Grindstone: Grind items into dust
 * <p>
 * TODO:
 * - Implement TileTelescope
 * - Implement TileGrindstone
 * - Implement machine functionality
 * - Implement custom rendering
 * - Implement particle effects
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

        // TODO: Initialize specific TileEntity based on type
        // switch (meta) {
        // case META_TELESCOPE:
        // // Initialize telescope
        // break;
        // case META_GRINDSTONE:
        // // Initialize grindstone
        // break;
        // }
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Open GUI based on machine type
        if (world.isRemote) {
            return true;
        }

        int meta = world.getBlockMetadata(x, y, z);

        // TODO: Open GUI for each machine type
        // switch (meta) {
        // case META_TELESCOPE:
        // // Open telescope GUI
        // break;
        // case META_GRINDSTONE:
        // // Open grindstone GUI
        // break;
        // }

        player.addChatMessage(
            new net.minecraft.util.ChatComponentText(
                "§6[Astral Sorcery] §rMachine GUI not yet implemented! Type: " + getMachineName(meta)));

        return true;

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        switch (meta) {
            case META_GRINDSTONE:
                return new hellfirepvp.astralsorcery.common.tile.TileGrindstone();
            case META_TELESCOPE:
                // TODO: Implement TileTelescope
                // return new hellfirepvp.astralsorcery.common.tile.TileTelescope();
                return new hellfirepvp.astralsorcery.common.tile.TileGrindstone(); // Placeholder
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
