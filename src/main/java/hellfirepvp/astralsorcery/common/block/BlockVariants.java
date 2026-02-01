/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Variants interface for blocks with multiple subtypes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

/**
 * BlockVariants interface (1.7.10)
 * <p>
 * Provides utility methods for blocks with multiple subtypes (metadata variants).
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No IBlockState - uses integer metadata (0-15)</li>
 * <li>No IProperty - metadata values directly</li>
 * <li>No default methods - Java 7 compatible</li>
 * <li>Uses BlockVariants.Helper for utility methods</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * 
 * <pre>
 * public class BlockMarble extends AstralBaseBlock implements BlockVariants {
 *     public static final String[] VARIANT_NAMES = {"raw", "bricks", "pillar", ...};
 *
 *     {@literal @}Override
 *     public String[] getVariantNames() {
 *         return VARIANT_NAMES;
 *     }
 *
 *     {@literal @}Override
 *     public String getStateName(int metadata) {
 *         return VARIANT_NAMES[metadata % VARIANT_NAMES.length];
 *     }
 * }
 * </pre>
 */
public interface BlockVariants {

    /**
     * Get all variant names for this block
     * <p>
     * Used for model registration, item registration, and resource binding.
     *
     * @return String array of variant names
     */
    String[] getVariantNames();

    /**
     * Get the name for a specific metadata value
     * <p>
     * Used for model files and resource paths.
     *
     * @param metadata Block metadata (0-15)
     * @return Variant name for this metadata
     */
    String getStateName(int metadata);

    /**
     * Helper class with utility methods for BlockVariants
     * <p>
     * Provides common functionality without needing default methods (Java 7 compatible)
     */
    public static class Helper {

        /**
         * Extract variant name from metadata array
         * <p>
         * Safely handles metadata bounds checking.
         *
         * @param metadata Block metadata
         * @param names    Variant names array
         * @return Variant name at metadata index
         */
        public static String extractVariantName(int metadata, String[] names) {
            int index = metadata % names.length;
            return names[index];
        }

        /**
         * Get all valid metadata values for a block
         * <p>
         * Returns array [0, 1, 2, ..., variantCount-1]
         *
         * @param variantCount Number of variants
         * @return Array of valid metadata values
         */
        public static int[] getValidMetadata(int variantCount) {
            int[] metadata = new int[variantCount];
            for (int i = 0; i < variantCount; i++) {
                metadata[i] = i;
            }
            return metadata;
        }

        /**
         * Get block name from class
         * <p>
         * Extracts simple class name (e.g., "BlockMarble")
         *
         * @param block Block instance
         * @return Simple class name
         */
        public static String getBlockName(Object block) {
            return block.getClass()
                .getSimpleName();
        }

        /**
         * Get full state name for registration
         * <p>
         * Combines block name and variant name.
         * <p>
         * Example: "BlockMarble_bricks"
         *
         * @param block       Block instance
         * @param variantName Variant name
         * @return Full state name
         */
        public static String getFullStateName(Object block, String variantName) {
            return getBlockName(block) + "_" + variantName;
        }
    }
}
