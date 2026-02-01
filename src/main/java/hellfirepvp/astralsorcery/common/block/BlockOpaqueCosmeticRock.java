/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Opaque cosmetic rock block - placeholder for future decorative blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.material.Material;

import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockOpaqueCosmeticRock - Opaque decorative rock block (1.7.10)
 * <p>
 * <b>NOTE:</b> Currently a placeholder with only NONE type.
 * Reserved for future decorative blocks.
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata (currently only 0)</li>
 * <li>No IBlockState - uses direct metadata</li>
 * <li>Simplified variant system</li>
 * </ul>
 * <p>
 * <b>Original Features (may not work in 1.7.10):</b>
 * <ul>
 * <li>Multiple variants (currently only NONE implemented)</li>
 * <li>Custom collision boxes</li>
 * <li>Advanced rendering</li>
 * </ul>
 */
public class BlockOpaqueCosmeticRock extends AstralBaseBlock {

    /**
     * Constructor
     * <p>
     * High hardness and resistance similar to obsidian
     */
    public BlockOpaqueCosmeticRock() {
        super(Material.rock);

        // Set block properties
        setHardness(2.0F);
        setResistance(20.0F); // High resistance
        setStepSound(soundTypeStone);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
        setHarvestLevel("pickaxe", 3); // Diamond tier

        // NOTE: 1.7.10 doesn't have MapColor.IRON
        // Using default rock color
    }

    /**
     * Get the block's display name
     *
     * @return Unlocalized name
     */
    // TODO: Implement variant names when multiple types are added

    // TODO: Implement variant system when needed
    // Original version had:
    // - getBoundingBox() - returns FULL_BLOCK_AABB
    // - getSubBlocks() - creative tab items
    // - getStateFromMeta() / getMetaFromState() - metadata conversion
    // - getIdentifierForMeta() - variant identifier

    // NOTE: These methods from original version may not work in 1.7.10:
    // - IBlockState (not available in 1.7.10)
    // - PropertyEnum (not available in 1.7.10)
    // - NonNullList (not available in 1.7.10)
    // - RayTraceResult (not available in 1.7.10)

    /**
     * Internal enum for block types
     * <p>
     * Currently only NONE - placeholder for future expansion
     */
    public static enum BlockType {

        /** Placeholder type - currently the only type */
        NONE;

        /**
         * Get the name of this type
         *
         * @return Type name
         */
        public String getName() {
            return name().toLowerCase();
        }

        /**
         * Get type from metadata
         *
         * @param meta Metadata value
         * @return BlockType
         */
        public static BlockType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                return NONE;
            }
            return values()[meta];
        }
    }
}
