/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Multi-texture block model support
 * Handles blocks with different textures for different faces
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Multi-texture block model for 1.7.10
 * <p>
 * Handles blocks that use different textures for different faces.
 * This replaces the 1.12.2 JSON model system.
 * <p>
 * Example: A pillar block with top/bottom/side textures:
 * 
 * <pre>
 * {@literal @}Override
 * public IIcon getIcon(int side, int meta) {
 *     return PillarModel.getIcon(this.icons, side, meta);
 * }
 * </pre>
 */
@SideOnly(Side.CLIENT)
public final class MultiTextureModel {

    private MultiTextureModel() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get icon for pillar-type blocks (top, bottom, side)
     *
     * @param icons Icon array: [bottom, top, side] or [side, top, bottom]
     * @param side  Block side (0=down, 1=up, 2=north, 3=south, 4=west, 5=east)
     * @return Appropriate icon
     */
    public static IIcon getPillarIcon(IIcon[] icons, int side) {
        if (icons == null || icons.length < 3) {
            return icons != null && icons.length > 0 ? icons[0] : null;
        }

        // side: 0=down, 1=up, 2-4=sides
        switch (side) {
            case 0: // down
                return icons[0]; // bottom
            case 1: // up
                return icons[1]; // top
            default: // sides (2=north, 3=south, 4=west, 5=east)
                return icons[2]; // side
        }
    }

    /**
     * Get icon for pillar with top/side/bottom ordering
     *
     * @param icons Icon array: [top, side, bottom]
     * @param side  Block side
     * @return Appropriate icon
     */
    public static IIcon getPillarIconTopSideBottom(IIcon[] icons, int side) {
        if (icons == null || icons.length < 3) {
            return icons != null && icons.length > 0 ? icons[0] : null;
        }

        switch (side) {
            case 0: // down
                return icons[2]; // bottom
            case 1: // up
                return icons[0]; // top
            default: // sides
                return icons[1]; // side
        }
    }

    /**
     * Get icon for cross-type blocks (same texture on all horizontal sides)
     *
     * @param icons Icon array: [top, bottom, side]
     * @param side  Block side
     * @return Appropriate icon
     */
    public static IIcon getCrossIcon(IIcon[] icons, int side) {
        if (icons == null || icons.length < 3) {
            return icons != null && icons.length > 0 ? icons[0] : null;
        }

        switch (side) {
            case 0: // down
                return icons[1]; // bottom
            case 1: // up
                return icons[0]; // top
            default: // sides
                return icons[2]; // side
        }
    }

    /**
     * Get icon for directional blocks (4-way rotation)
     *
     * @param icons Icon array: [north, east, south, west, top, bottom]
     * @param side  Block side
     * @param meta  Block metadata
     * @return Appropriate icon based on rotation
     */
    public static IIcon getDirectionalIcon(IIcon[] icons, int side, int meta) {
        if (icons == null || icons.length < 6) {
            return icons != null && icons.length > 0 ? icons[0] : null;
        }

        // Adjust for block rotation
        int rotation = meta & 3; // 4 rotation states

        switch (side) {
            case 0: // down
                return icons[5]; // bottom
            case 1: // up
                return icons[4]; // top
            case 2: // north
                return icons[(0 + rotation) % 4];
            case 3: // south
                return icons[(2 + rotation) % 4];
            case 4: // west
                return icons[(3 + rotation) % 4];
            case 5: // east
                return icons[(1 + rotation) % 4];
            default:
                return icons[0];
        }
    }

    /**
     * Get icon for blocks with metadata-based texture selection
     *
     * @param icons          Icon array
     * @param side           Block side
     * @param meta           Block metadata
     * @param metadataToIcon Function to map metadata to icon index
     * @return Appropriate icon
     */
    public static IIcon getMetadataIcon(IIcon[] icons, int side, int meta, MetadataToIconFunction metadataToIcon) {
        if (icons == null || icons.length == 0) {
            return null;
        }

        int iconIndex = metadataToIcon.getIconIndex(meta, side);
        if (iconIndex < 0 || iconIndex >= icons.length) {
            iconIndex = 0;
        }

        return icons[iconIndex];
    }

    /**
     * Functional interface for metadata to icon mapping
     */
    @SideOnly(Side.CLIENT)
    public interface MetadataToIconFunction {

        /**
         * Get icon index based on metadata and side
         *
         * @param meta Block metadata
         * @param side Block side
         * @return Icon index in the icons array
         */
        int getIconIndex(int meta, int side);
    }
}
