/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Infused wood block - Starlight-infused wood with 7 variants
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.model.MultiTextureModel;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * BlockInfusedWood - Starlight-infused wood (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>7 variants: RAW, PLANKS, COLUMN, ARCH, ENGRAVED, ENRICHED, INFUSED</li>
 * <li>Wood material with starlight infusion</li>
 * <li>COLUMN variant has thinner collision box</li>
 * <li>COLUMN variants are translucent (allow light through)</li>
 * <li>Harvested with axe</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata (0-6 for variants)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>No getActualState() - 1.8+ method, pillar connection simplified</li>
 * <li>No BlockStateContainer - 1.8+ system</li>
 * <li>COLUMN: thinner collision box (0.25-0.75 instead of 0-1)</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Differences from BlockMarble:
 * <ul>
 * <li>Material: Wood instead of Rock</li>
 * <li>Sound: Wood instead of Stone</li>
 * <li>Harvest tool: Axe instead of Pickaxe</li>
 * <li>COLUMN variant: Thinner collision box (special pillar design)</li>
 * <li>Variant names: PLANKS/COLUMN/ENRICHED/INFUSED instead of BRICKS/PILLAR/RUNED</li>
 * </ul>
 * <p>
 * <b>Variants:</b>
 * 
 * <pre>
 * 0 = RAW        - Raw infused log
 * 1 = PLANKS     - Infused planks
 * 2 = COLUMN     - Pillar middle (thinner, translucent)
 * 3 = ARCH       - Arch infused wood
 * 4 = ENGRAVED   - Engraved infused wood
 * 5 = ENRICHED   - Enriched infused wood
 * 6 = INFUSED    - Fully infused wood
 * </pre>
 */
public class BlockInfusedWood extends AstralBaseBlock implements BlockCustomName, BlockVariants {

    /** Render ID for column OBJ rendering (set by AstralRenderLoader) */
    public static int COLUMN_RENDER_ID = 0;

    /** Variant names */
    private static final String[] VARIANT_NAMES = { "raw", // 0
        "planks", // 1
        "column", // 2
        "column_top", // 3 - dynamic column top
        "column_bottom", // 4 - dynamic column bottom
        "arch", // 5
        "engraved", // 6
        "enriched", // 7
        "infused" // 8
    };

    @SideOnly(Side.CLIENT)
    private Map<Integer, IIcon> iconMap;

    @SideOnly(Side.CLIENT)
    private IIcon columnTop;
    @SideOnly(Side.CLIENT)
    private IIcon columnBottom;

    /**
     * Enum for infused wood types
     */
    public static enum WoodType {

        /** Raw infused log */
        RAW(0, true),
        /** Infused planks */
        PLANKS(1, true),
        /** Column middle (thinner, translucent) */
        COLUMN(2, true),
        /** Column top (dynamic - when no column above) */
        COLUMN_TOP(3, false),
        /** Column bottom (dynamic - when no column below) */
        COLUMN_BOTTOM(4, false),
        /** Arch infused wood */
        ARCH(5, true),
        /** Engraved infused wood */
        ENGRAVED(6, true),
        /** Enriched infused wood */
        ENRICHED(7, true),
        /** Fully infused wood */
        INFUSED(8, true);

        private final int meta;
        private final boolean obtainable;

        WoodType(int meta, boolean obtainable) {
            this.meta = meta;
            this.obtainable = obtainable;
        }

        public int getMetadata() {
            return meta;
        }

        public boolean isObtainable() {
            return obtainable;
        }

        /**
         * Is this a column variant?
         * Column variants are translucent and have thinner collision box.
         */
        public boolean isColumn() {
            return this == COLUMN || this == COLUMN_TOP || this == COLUMN_BOTTOM;
        }

        /**
         * Get wood type from metadata
         */
        public static WoodType byMetadata(int meta) {
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

    /**
     * Constructor
     */
    public BlockInfusedWood() {
        super(Material.wood);

        setHardness(1.0F);
        setResistance(3.0F);
        setStepSound(soundTypeWood);
        setHarvestLevel("axe", 0); // Any tier (wood/gold/stone/iron/diamond)
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.BROWN specifically
        // Using default wood color
    }

    /**
     * Get render type
     * <p>
     * Returns the column OBJ renderer render ID for column variants (metadata 2, 3, 4)
     * Returns default render type (0) for all other variants
     */
    @Override
    public int getRenderType() {
        // Note: This method doesn't have access to metadata in 1.7.10
        // We return the column render ID, and the renderer will check metadata
        // For non-column variants, they use standard icon-based rendering
        return COLUMN_RENDER_ID;
    }

    /**
     * Get damage value for dropped item
     */
    public int damageDropped(int meta) {
        return meta; // Return metadata to preserve variant
    }

    /**
     * Get sub blocks for creative tab
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Add all obtainable variants
        for (WoodType type : WoodType.values()) {
            if (type.isObtainable()) {
                list.add(new ItemStack(item, 1, type.getMetadata()));
            }
        }
    }

    /**
     * Is this a full block?
     * <p>
     * Column variants are translucent (not opaque)
     * All other variants are standard opaque blocks
     */
    @Override
    public boolean isOpaqueCube() {
        // This method doesn't have access to metadata in 1.7.10
        // Column transparency is handled through getLightOpacity() and shouldSideBeRendered()
        return true; // Default to true, TESR blocks handle their own rendering
    }

    /**
     * Does this block render normally?
     */
    @Override
    public boolean renderAsNormalBlock() {
        // Only column variant needs custom rendering (translucent)
        // All other variants use standard block rendering
        return true; // Use standard rendering for all wood types
    }

    /**
     * Get light opacity
     * <p>
     * Column variants are translucent (opacity 0)
     * All other variants are opaque (opacity 255)
     */
    @Override
    public int getLightOpacity() {
        // This method doesn't have access to metadata in 1.7.10
        return 255; // Default opaque
    }

    /**
     * Get light opacity for a specific block position
     */
    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WoodType type = WoodType.byMetadata(meta);
        return type.isColumn() ? 0 : 255; // Columns are translucent
    }

    /**
     * Set block bounds based on state
     * <p>
     * All column variants have thinner bounds (0.25-0.75)
     * Other variants use standard full block bounds
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WoodType type = WoodType.byMetadata(meta);
        if (type.isColumn()) {
            // Thinner column: 0.25 to 0.75 (50% width)
            this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Get collision bounding box
     * <p>
     * All column variants have thinner collision
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WoodType type = WoodType.byMetadata(meta);
        if (type.isColumn()) {
            return AxisAlignedBB.getBoundingBox(x + 0.25, y, z + 0.25, x + 0.75, y + 1, z + 0.75);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
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

        // BlockCustomName implementation
    }

    public String getIdentifierForMeta(int meta) {
        return WoodType.byMetadata(meta)
            .getName();

        // BlockVariants implementation
    }

    public String[] getVariantNames() {
        return VARIANT_NAMES;

    }

    public String getStateName(int metadata) {
        return WoodType.byMetadata(metadata)
            .getName();

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ getActualState(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ method
        // - Used for pillar auto-connection
        // - 1.7.10: Would require onNeighborChange metadata manipulation
        //
        // ❌ getBoundingBox(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ signature
        // - 1.7.10: setBlockBounds() / getCollisionBoundingBoxFromPool()
        // - COLUMN has thinner bounds (0.25-0.75)
        //
        // ❌ causesSuffocation(IBlockState)
        // - 1.8+ method
        // - COLUMN doesn't cause suffocation
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
        // - 1.7.10 uses metadata directly
        //
        // ❌ createBlockState()
        // - 1.8+ method
        // - 1.7.10 doesn't use BlockStateContainer

        // NOTE: 1.7.10 metadata layout:
        // 0 = RAW
        // 1 = PLANKS
        // 2 = COLUMN (middle, translucent, thinner collision)
        // 3 = ARCH
        // 4 = ENGRAVED
        // 5 = ENRICHED
        // 6 = INFUSED
    }

    /**
     * Get wood type from metadata
     * Convenience method
     *
     * @param meta Block metadata
     * @return WoodType
     */
    public static WoodType getTypeFromMeta(int meta) {
        return WoodType.byMetadata(meta);
    }

    /**
     * Called when a neighbor block changes
     * Handles dynamic column connection (COLUMN <-> COLUMN_TOP <-> COLUMN_BOTTOM)
     * <p>
     * NOTE: 1.7.10 does not use @Override for this method
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            WoodType type = WoodType.byMetadata(meta);

            if (type.isColumn()) {
                boolean columnAbove = isColumnAt(world, x, y + 1, z);
                boolean columnBelow = isColumnAt(world, x, y - 1, z);

                int newMeta;
                if (columnAbove && columnBelow) {
                    newMeta = WoodType.COLUMN.getMetadata(); // Middle section
                } else if (!columnAbove && columnBelow) {
                    newMeta = WoodType.COLUMN_TOP.getMetadata(); // Top section
                } else if (columnAbove && !columnBelow) {
                    newMeta = WoodType.COLUMN_BOTTOM.getMetadata(); // Bottom section
                } else {
                    newMeta = WoodType.COLUMN.getMetadata(); // Single column block
                }

                if (newMeta != meta) {
                    world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
                }
            }
        }
    }

    /**
     * Check if there's a column block at the given position
     */
    private boolean isColumnAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == this) {
            int meta = world.getBlockMetadata(x, y, z);
            return WoodType.byMetadata(meta)
                .isColumn();
        }
        return false;
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // Register main variant icons using Map-based approach (TST-style)
        iconMap = IconHelper.registerVariantIconMap(reg, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, meta -> {
            // Map metadata to texture name
            switch (meta) {
                case 0:
                    return "wood_raw";
                case 1:
                    return "wood_planks";
                case 2:
                    return "wood_column"; // Side texture for column middle
                case 3:
                    return "wood_column"; // Side texture for column top (same as middle)
                case 4:
                    return "wood_column"; // Side texture for column bottom (same as middle)
                case 5:
                    return "wood_arch";
                case 6:
                    return "wood_engraved";
                case 7:
                    return "wood_enriched";
                case 8:
                    return "wood_infused";
                default:
                    return "wood_raw";
            }
        });

        // Register column top and bottom textures
        columnTop = TextureRegister.registerBlockIcon(reg, "wood_column_top");
        columnBottom = TextureRegister.registerBlockIcon(reg, "wood_column_bottom");

        // Set default block icon
        this.blockIcon = iconMap.get(0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        // Safety check: if iconMap not initialized yet, return null
        if (iconMap == null) {
            hellfirepvp.astralsorcery.common.util.LogHelper.warn(
                "[BlockInfusedWood] iconMap is null! registerBlockIcons() may not have been called. side=" + side
                    + " meta="
                    + meta);
            return null;
        }

        // Clamp metadata to valid range
        if (meta < 0 || meta >= 9) {
            meta = 0;
        }

        // All column variants (COLUMN, COLUMN_TOP, COLUMN_BOTTOM) use different textures for top/bottom/side
        if (meta == WoodType.COLUMN.getMetadata() || meta == WoodType.COLUMN_TOP.getMetadata()
            || meta == WoodType.COLUMN_BOTTOM.getMetadata()) {
            IIcon columnSide = iconMap.get(meta);
            IIcon[] columnIcons = { columnBottom, columnTop, columnSide }; // [bottom, top, side]
            return MultiTextureModel.getPillarIcon(columnIcons, side);
        }

        // All other variants use single texture
        return IconHelper.getIconFromMap(iconMap, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return getIcon(side, meta);
    }
}
