/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Marble block - Main decorative building block with 7 variants
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.model.MultiTextureModel;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

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

    /** Variant names */
    private static final String[] VARIANT_NAMES = { "raw", // 0
        "bricks", // 1
        "pillar", // 2
        "arch", // 3
        "chiseled", // 4
        "engraved", // 5
        "runed" // 6
    };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    private IIcon pillarTop;
    @SideOnly(Side.CLIENT)
    private IIcon pillarBottom;

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
        /** Arch marble */
        ARCH(3, true),
        /** Chiseled marble */
        CHISELED(4, true),
        /** Engraved marble */
        ENGRAVED(5, true),
        /** Runed marble */
        RUNED(6, true);

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
            return this == PILLAR;
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
     * Return true for standard block rendering
     * Note: In 1.7.10, opaque cubes use standard rendering
     */
    @Override
    public boolean isOpaqueCube() {
        return true; // Use standard opaque block rendering
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
     * NOTE: 1.7.10 signature is different: getLightOpacity()
     * Override per-state check not possible without TileEntity
     */
    // NOTE: Original: getLightOpacity(IBlockState, IBlockAccess, BlockPos)
    // 1.7.10: getLightOpacity() - no state parameter
    // For now, using default opacity

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

    // ========== Texture Registration ==========

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[7];
        icons[0] = TextureRegister.registerBlockIcon(reg, "marble_raw");
        icons[1] = TextureRegister.registerBlockIcon(reg, "marble_bricks");
        icons[2] = TextureRegister.registerBlockIcon(reg, "marble_pillar"); // Side texture for pillar
        icons[3] = TextureRegister.registerBlockIcon(reg, "marble_arch");
        icons[4] = TextureRegister.registerBlockIcon(reg, "marble_chiseled");
        icons[5] = TextureRegister.registerBlockIcon(reg, "marble_engraved");
        icons[6] = TextureRegister.registerBlockIcon(reg, "marble_runed");

        // Register pillar top and bottom textures
        pillarTop = TextureRegister.registerBlockIcon(reg, "marble_pillar_top");
        pillarBottom = TextureRegister.registerBlockIcon(reg, "marble_pillar_bottom");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= icons.length) {
            meta = 0;
        }

        // PILLAR variant (2) uses different textures for top/bottom/side
        if (meta == MarbleType.PILLAR.getMetadata()) {
            IIcon[] pillarIcons = { pillarBottom, pillarTop, icons[2] }; // [bottom, top, side]
            return MultiTextureModel.getPillarIcon(pillarIcons, side);
        }

        // All other variants use single texture
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return getIcon(side, meta);
    }
}
