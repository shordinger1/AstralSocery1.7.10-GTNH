/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Altar block - Basic celestial altar
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.model.MultiTextureModel;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * BlockAltar - Basic celestial altar (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Basic ritual altar for starlight crafting</li>
 * <li>Has TileEntity (TileAltar)</li>
 * <li>Right-click to open GUI</li>
 * <li>Handles item recipes</li>
 * <li>Part of constellation discovery system</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>Material.rock (stone altar)</li>
 * <li>No IBlockState - simple block</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Complex functional block
 * <ul>
 * <li>GUI system - need to implement</li>
 * <li>Recipe system - constellation discovery</li>
 * <li>Ritual mechanics - starlight interaction</li>
 * <li>TileAltar with inventory and crafting logic</li>
 * </ul>
 */
public class BlockAltar extends BlockContainer {

    /**
     * Enum for altar types (corresponds to texture sets)
     * <p>
     * Maps to TileAltar.AltarLevel:
     * - DISCOVERY → Altar_1
     * - ATTUNEMENT → Altar_2
     * - CONSTELLATION_CRAFT → Altar_3
     * - TRAIT_CRAFT → Altar_4
     * - BRILLIANCE → Altar_4 (reuses texture)
     */
    public static enum AltarType {

        DISCOVERY(0, "altar_1"),
        ATTUNEMENT(1, "altar_2"),
        CONSTELLATION_CRAFT(2, "altar_3"),
        TRAIT_CRAFT(3, "altar_4"),
        BRILLIANCE(4, "altar_4"); // Reuses Altar_4 texture

        private final int meta;
        private final String texturePrefix;

        AltarType(int meta, String texturePrefix) {
            this.meta = meta;
            this.texturePrefix = texturePrefix;
        }

        public int getMetadata() {
            return meta;
        }

        public String getTexturePrefix() {
            return texturePrefix;
        }

        /**
         * Get altar type from metadata
         */
        public static AltarType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }

        /**
         * Get the name of this type
         */
        public String getName() {
            return name().toLowerCase();
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon[][] altarIcons; // [altarType][face] where face: 0=bottom, 1=top, 2=side

    /**
     * Constructor
     */
    public BlockAltar() {
        super(Material.rock);

        setHardness(3.5F);
        setResistance(20.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1); // Stone tier
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    /**
     * Get sub-blocks for creative tab
     * <p>
     * Adds all 5 altar variants (metadata 0-4) to creative tab
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (AltarType type : AltarType.values()) {
            list.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    /**
     * Is this a full block?
     * Altar has custom model, so it's not a full cube
     */
    @Override
    public boolean isOpaqueCube() {
        return false; // Custom 3D model
    }

    /**
     * Does this block render normally?
     */
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
     * On block activated - open GUI
     * <p>
     * Phase 3.1: Opens ModularUI GUI
     * Phase 4: Shift+Right-Click opens recipe viewer
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Phase 4: Shift+Right-Click opens recipe viewer
        if (player.isSneaking()) {
            if (world.isRemote) {
                // Open recipe viewer on client side
                cpw.mods.fml.common.FMLCommonHandler.instance()
                    .showGuiScreen(new hellfirepvp.astralsorcery.client.gui.AltarRecipeViewer());
            }
            return true;
        }

        // Normal Right-Click opens altar GUI
        if (world.isRemote) {
            return true; // Client side
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAltar) {
            TileAltar altar = (TileAltar) te;
            // Open ModularUI GUI
            TileEntityGuiFactory.INSTANCE.open(player, altar);
            return true;
        }
        return false;
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
     * <p>
     * Note: Inventory contents are dropped by breakBlock(), not by this method.
     * This method only returns the altar block itself.
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        // Drop the altar block with its metadata (altar level)
        drops.add(new ItemStack(this, 1, metadata));
        return drops;
    }

    /**
     * Break block
     * <p>
     * Phase 3.1: Drops inventory contents
     */
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        // Drop inventory contents
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAltar) {
            TileAltar altar = (TileAltar) te;
            // Drop items from inventory
            for (int i = 0; i < altar.getInventory()
                .getSlots(); i++) {
                ItemStack stack = altar.getInventory()
                    .getStackInSlot(i);
                if (stack != null) {
                    // Drop item
                    net.minecraft.entity.item.EntityItem entityItem = new net.minecraft.entity.item.EntityItem(
                        world,
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        stack);
                    entityItem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityItem);
                }
            }
        }
        super.breakBlock(world, x, y, z, blockBroken, meta);
    }

    /**
     * On block placed - sync altar level from item metadata
     * <p>
     * When player places an altar item, the metadata (0-4) determines the altar level.
     * This is already synced to the TileEntity via createNewTileEntity().
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);

        // Note: Altar level is already set in createNewTileEntity() based on metadata
        // No additional sync needed here.
    }

    /**
     * Create new tile entity
     * <p>
     * Phase 3.1: Returns TileAltar with appropriate level based on metadata
     * <p>
     * Metadata mapping:
     * - 0: DISCOVERY (Altar_1)
     * - 1: ATTUNEMENT (Altar_2)
     * - 2: CONSTELLATION_CRAFT (Altar_3)
     * - 3: TRAIT_CRAFT (Altar_4)
     * - 4: BRILLIANCE (Altar_4)
     */
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        AltarType type = AltarType.byMetadata(meta);
        TileAltar.AltarLevel level;

        LogHelper.info("[BlockAltar] createNewTileEntity called with metadata: " + meta + ", type: " + type);

        // Map AltarType to AltarLevel
        switch (type) {
            case DISCOVERY:
                level = TileAltar.AltarLevel.DISCOVERY;
                break;
            case ATTUNEMENT:
                level = TileAltar.AltarLevel.ATTUNEMENT;
                break;
            case CONSTELLATION_CRAFT:
                level = TileAltar.AltarLevel.CONSTELLATION_CRAFT;
                break;
            case TRAIT_CRAFT:
            case BRILLIANCE:
                level = TileAltar.AltarLevel.TRAIT_CRAFT;
                break;
            default:
                level = TileAltar.AltarLevel.DISCOVERY;
                break;
        }

        LogHelper.info("[BlockAltar] Creating TileAltar with level: " + level);
        return new TileAltar(level);
    }

    /**
     * NOTE: Altar system
     * <p>
     * 1.7.10 Implementation Status:
     * - ✓ TileAltar: Inventory and crafting logic implemented with ItemStackHandler
     * - ✓ ModularUI GUI: Client/server GUI implemented with ModularUI system
     * - ✓ Recipe System: AltarRecipeRegistry with 38+ recipes
     * - ✓ Constellation Discovery: Basic discovery for DISCOVERY level altar
     * - ✓ Starlight Collection: StarlightHelper for starlight collection
     * - ✓ Starlight Consumption: consumeStarlight() method in TileAltar
     */

    // ========== Texture Registration ==========

    /**
     * Register block icons for all altar types
     * <p>
     * Each altar type has 3 textures: bottom, top, side
     * Loaded from textures/models/altar/
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        altarIcons = new IIcon[AltarType.values().length][3];

        // Register icons for each altar type
        for (AltarType type : AltarType.values()) {
            String prefix = "altar/" + type.getTexturePrefix();
            altarIcons[type.ordinal()][0] = TextureRegister.registerModelIcon(reg, prefix + "_bottom");
            altarIcons[type.ordinal()][1] = TextureRegister.registerModelIcon(reg, prefix + "_top");
            altarIcons[type.ordinal()][2] = TextureRegister.registerModelIcon(reg, prefix + "_side");
        }
    }

    /**
     * Get icon for rendering
     * <p>
     * Returns appropriate icon based on metadata (altar type) and face (side)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        AltarType type = AltarType.byMetadata(meta);
        if (altarIcons == null || altarIcons[type.ordinal()] == null) {
            return null;
        }

        IIcon[] icons = altarIcons[type.ordinal()]; // [bottom, top, side]
        return MultiTextureModel.getPillarIcon(icons, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return getIcon(side, meta);
    }
}
