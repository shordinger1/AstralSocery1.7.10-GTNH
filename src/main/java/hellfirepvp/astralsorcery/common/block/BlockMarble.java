/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Marble block - Main decorative building block with 7 variants
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
 * BlockMarble - Marble decorative block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>7 variants: RAW, BRICKS, PILLAR, ARCH, CHISELED, ENGRAVED, RUNED</li>
 * <li>Pillar variants are translucent (allow light through)</li>
 * <li>Simplified implementation (no dynamic pillar connection)</li>
 * <li>Metadata-based variant system</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata (0-6 for variants)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>No getActualState() - 1.8+ method, pillar connection simplified</li>
 * <li>No BlockStateContainer - 1.8+ system</li>
 * <li>Pillar variants: isOpaqueCube() returns false, getLightOpacity() returns 0</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had:
 * <ul>
 * <li>getActualState() for dynamic pillar connection (1.8+)</li>
 * <li>PILLAR_TOP/PILLAR_BOTTOM auto-detection (1.8+)</li>
 * <li>doesSideBlockRendering() - 1.8+ method</li>
 * <li>isFullBlock(), isTopSolid() - 1.8+ methods</li>
 * <li>BlockFaceShape - 1.8+ enum</li>
 * </ul>
 * <p>
 * <b>Simplified in 1.7.10:</b>
 * <ul>
 * <li>PILLAR_TOP and PILLAR_BOTTOM are separate metadata values</li>
 * <li>Players manually place pillar variants instead of auto-connection</li>
 * <li>All 7 variants available in creative mode</li>
 * </ul>
 * <p>
 * <b>Variants:</b>
 *
 * <pre>
 * 0 = RAW        - Raw marble
 * 1 = BRICKS     - Brick marble
 * 2 = PILLAR     - Pillar middle (translucent)
 * 3 = ARCH       - Arch marble
 * 4 = CHISELED   - Chiseled marble
 * 5 = ENGRAVED   - Engraved marble
 * 6 = RUNED      - Runed marble
 * </pre>
 */
public class BlockMarble extends AstralBaseBlock implements BlockCustomName, BlockVariants {

    /** Render ID for pillar OBJ rendering (set by AstralRenderLoader) */
    public static int PILLAR_RENDER_ID = 0;

    /** Variant names */
    private static final String[] VARIANT_NAMES = { "raw", // 0
        "bricks", // 1
        "pillar", // 2
        "pillar_top", // 3 - dynamic pillar top
        "pillar_bottom", // 4 - dynamic pillar bottom
        "arch", // 5
        "chiseled", // 6
        "engraved", // 7
        "runed" // 8
    };

    @SideOnly(Side.CLIENT)
    private Map<Integer, IIcon> iconMap;

    @SideOnly(Side.CLIENT)
    private IIcon pillarTop;
    @SideOnly(Side.CLIENT)
    private IIcon pillarBottom;
    @SideOnly(Side.CLIENT)
    private IIcon updown;
    /**
     * Enum for marble types
     */
    public static enum MarbleType {

        /** Raw marble stone */
        RAW(0, true),
        /** Marble bricks */
        BRICKS(1, true),
        /** Pillar middle (translucent) */
        PILLAR(2, true),
        /** Pillar top (dynamic - when no pillar above) */
        PILLAR_TOP(3, false),
        /** Pillar bottom (dynamic - when no pillar below) */
        PILLAR_BOTTOM(4, false),
        /** Arch marble */
        ARCH(5, true),
        /** Chiseled marble */
        CHISELED(6, true),
        /** Engraved marble */
        ENGRAVED(7, true),
        /** Runed marble */
        RUNED(8, true);

        private final int meta;
        private final boolean obtainable;

        MarbleType(int meta, boolean obtainable) {
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
         * Is this a pillar variant?
         * Pillar variants are translucent.
         */
        public boolean isPillar() {
            return this == PILLAR || this == PILLAR_TOP || this == PILLAR_BOTTOM;
        }

        /**
         * Get marble type from metadata
         */
        public static MarbleType byMetadata(int meta) {
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
    public BlockMarble() {
        super(Material.rock);

        setHardness(1.0F);
        setResistance(3.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1); // Stone tier
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.GRAY
        // Using default rock color
    }

    /**
     * Get render type
     * <p>
     * Returns the pillar OBJ renderer render ID for pillar variants (metadata 2, 3, 4)
     * Returns default render type (0) for all other variants
     */
    @Override
    public int getRenderType() {
        // Note: This method doesn't have access to metadata in 1.7.10
        // We return the pillar render ID, and the renderer will check metadata
        // For non-pillar variants, they use standard icon-based rendering
        return PILLAR_RENDER_ID;
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
        for (MarbleType type : MarbleType.values()) {
            if (type.isObtainable()) {
                list.add(new ItemStack(item, 1, type.getMetadata()));
            }
        }
    }

    /**
     * Is this a full block?
     * <p>
     * Pillar variants are translucent (not opaque)
     * All other variants are standard opaque blocks
     */
    @Override
    public boolean isOpaqueCube() {
        // This method doesn't have access to metadata in 1.7.10
        // Pillar transparency is handled through getLightOpacity() and shouldSideBeRendered()
        return true; // Default to true, TESR blocks handle their own rendering
    }

    /**
     * Does this block render normally?
     */
    @Override
    public boolean renderAsNormalBlock() {
        // Only pillar variant needs custom rendering (translucent)
        // All other variants use standard block rendering
        return true; // Use standard rendering for all marble types
    }

    /**
     * Get light opacity
     * <p>
     * Pillar variants are translucent (opacity 0)
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
        MarbleType type = MarbleType.byMetadata(meta);
        return type.isPillar() ? 0 : 255; // Pillars are translucent
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
        return MarbleType.byMetadata(meta)
            .getName();

        // BlockVariants implementation
    }

    public String[] getVariantNames() {
        return VARIANT_NAMES;

    }

    public String getStateName(int metadata) {
        return MarbleType.byMetadata(metadata)
            .getName();

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ getActualState(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ method
        // - Used for pillar auto-connection
        // - 1.7.10: Would require onNeighborChange metadata manipulation
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
        // - 1.7.10 uses metadata directly
        //
        // ❌ createBlockState()
        // - 1.8+ method
        // - 1.7.10 doesn't use BlockStateContainer
        //
        // ❌ doesSideBlockRendering(IBlockState, IBlockAccess, BlockPos, EnumFacing)
        // - 1.8+ method
        // - 1.7.10 uses shouldSideBeRendered()
        //
        // ❌ isFullBlock(IBlockState), isTopSolid(IBlockState)
        // - 1.8+ methods
        //
        // ❌ getBlockFaceShape()
        // - 1.8+ method

        // NOTE: Pillar connection system
        //
        // Original version uses getActualState() to dynamically switch between:
        // - PILLAR (middle, when both up and down are pillars)
        // - PILLAR_TOP (top, when above is NOT pillar)
        // - PILLAR_BOTTOM (bottom, when below is NOT pillar)
        //
        // In 1.7.10, we have two options:
        // 1. Use separate metadata values (simpler, player chooses)
        // 2. Use onNeighborChange() to update metadata (complex, may cause lag)
        //
        // Current implementation: Option 1 (simpler)
        // All 7 variants are manually placeable

        // NOTE: 1.7.10 metadata layout:
        // 0 = RAW
        // 1 = BRICKS
        // 2 = PILLAR (middle, translucent)
        // 3 = ARCH
        // 4 = CHISELED
        // 5 = ENGRAVED
        // 6 = RUNED
        //
        // Original also had:
        // PILLAR_TOP (visual only, not in creative)
        // PILLAR_BOTTOM (visual only, not in creative)
        //
        // In 1.7.10 simplified version, we don't use these auto-generated states.
    }

    /**
     * Get marble type from metadata
     * Convenience method
     *
     * @param meta Block metadata
     * @return MarbleType
     */
    public static MarbleType getTypeFromMeta(int meta) {
        return MarbleType.byMetadata(meta);
    }

    /**
     * Called when a neighbor block changes
     * Handles dynamic pillar connection (PILLAR <-> PILLAR_TOP <-> PILLAR_BOTTOM)
     * <p>
     * NOTE: 1.7.10 does not use @Override for this method
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            MarbleType type = MarbleType.byMetadata(meta);

            if (type.isPillar()) {
                boolean pillarAbove = isPillarAt(world, x, y + 1, z);
                boolean pillarBelow = isPillarAt(world, x, y - 1, z);

                int newMeta;
                if (pillarAbove && pillarBelow) {
                    newMeta = MarbleType.PILLAR.getMetadata(); // Middle section
                } else if (!pillarAbove && pillarBelow) {
                    newMeta = MarbleType.PILLAR_TOP.getMetadata(); // Top section
                } else if (pillarAbove && !pillarBelow) {
                    newMeta = MarbleType.PILLAR_BOTTOM.getMetadata(); // Bottom section
                } else {
                    newMeta = MarbleType.PILLAR.getMetadata(); // Single pillar block
                }

                if (newMeta != meta) {
                    world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
                }
            }
        }
    }

    /**
     * Check if there's a pillar block at the given position
     */
    private boolean isPillarAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == this) {
            int meta = world.getBlockMetadata(x, y, z);
            return MarbleType.byMetadata(meta)
                .isPillar();
        }
        return false;
    }

    // ========== Texture Registration ==========

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        // Register main variant icons using Map-based approach (TST-style)
        iconMap = IconHelper.registerVariantIconMap(reg, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, meta -> {
            // Map metadata to texture name
            switch (meta) {
                case 0:
                    return "marble_raw";
                case 1:
                    return "marble_bricks";
                case 2:
                    return "marble_pillar"; // Side texture for pillar middle
                case 3:
                    return "marble_pillar"; // Side texture for pillar top (same as middle)
                case 4:
                    return "marble_pillar"; // Side texture for pillar bottom (same as middle)
                case 5:
                    return "marble_arch";
                case 6:
                    return "marble_chiseled";
                case 7:
                    return "marble_engraved";
                case 8:
                    return "marble_runed";
                default:
                    return "marble_raw";
            }
        });

        // Register pillar top and bottom textures (for pillar ends)
        pillarTop = TextureRegister.registerBlockIcon(reg, "marble_pillar_top");
        pillarBottom = TextureRegister.registerBlockIcon(reg, "marble_pillar_bottom");
        updown = TextureRegister.registerBlockIcon(reg,"marble_pillar_updown");
        // Set default block icon
        this.blockIcon = iconMap.get(0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        // Safety check: if iconMap not initialized yet, return null
        if (iconMap == null) {
            hellfirepvp.astralsorcery.common.util.LogHelper.warn(
                "[BlockMarble] iconMap is null! registerBlockIcons() may not have been called. side=" + side
                    + " meta="
                    + meta);
            return null;
        }

        // Clamp metadata to valid range
        if (meta < 0 || meta >= 9) {
            meta = 0;
        }

        // All pillar variants (PILLAR, PILLAR_TOP, PILLAR_BOTTOM) use different textures for top/bottom/side
        if (meta == MarbleType.PILLAR.getMetadata() || meta == MarbleType.PILLAR_TOP.getMetadata()
            || meta == MarbleType.PILLAR_BOTTOM.getMetadata()) {
            IIcon pillarSide = iconMap.get(meta);
            IIcon[] pillarIcons = { pillarBottom, pillarTop, pillarSide,updown }; // [bottom, top, side]
            return MultiTextureModel.getPillarIcon(pillarIcons, side);
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
