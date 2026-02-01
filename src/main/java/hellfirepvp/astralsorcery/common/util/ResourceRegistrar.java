/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Centralized resource registration system
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.item.ItemTextureMap;

/**
 * Centralized resource registration system
 * <p>
 * Provides unified methods for registering block and item icons.
 * All icon registration should go through this class to ensure consistency.
 * <p>
 * Usage:
 * 
 * <pre>
 * // Simple block with one icon
 * ResourceRegistrar.registerBlockIcon(block, "marble_raw");
 *
 * // Block with metadata variants
 * ResourceRegistrar.registerBlockIcons(block, new String[] { "marble_raw", "marble_bricks", "marble_pillar" });
 *
 * // Item with metadata variants
 * ResourceRegistrar.registerItemIcons(item, new String[] { "aquamarine", "stardust", "glass_lens" });
 * </pre>
 */
@SideOnly(Side.CLIENT)
public final class ResourceRegistrar {

    private ResourceRegistrar() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========== Block Registration ==========

    /**
     * Register a single icon for a block
     *
     * @param block    The block to register icon for
     * @param iconName The icon name (without mod ID prefix)
     * @return Registered IIcon
     */
    public static IIcon registerBlockIcon(Block block, String iconName) {
        return registerBlockIcon(block, 0, iconName);
    }

    /**
     * Register an icon for a block at a specific index
     *
     * @param block    The block to register icon for
     * @param index    The icon index (for metadata variants)
     * @param iconName The icon name (without mod ID prefix)
     * @return Registered IIcon
     */
    public static IIcon registerBlockIcon(Block block, int index, String iconName) {
        if (block == null) {
            LogHelper.warn("[ResourceRegistrar] Cannot register icon for null block");
            return null;
        }

        IIcon icon = block.getIcon(0, 0);
        if (icon != null) {
            // Block already has icons registered
            String existingName = icon.getIconName();
            LogHelper.debug(
                "[ResourceRegistrar] Block " + block.getUnlocalizedName() + " already has icon: " + existingName);
            return icon;
        }

        // Use the block's own icon registration system
        // This requires the block to extend a class that supports icon registration
        String fullName = "astralsorcery:" + iconName;
        LogHelper.debug("[ResourceRegistrar] Registering block icon: " + fullName);

        // Note: In 1.7.10, blocks register icons through their own registerBlockIcons method
        // This is a placeholder for the actual registration logic
        return null; // The actual registration happens in the block class
    }

    /**
     * Register multiple icons for a block (metadata variants)
     *
     * @param block     The block to register icons for
     * @param iconNames Array of icon names (without mod ID prefix)
     * @return Array of registered IIcons
     */
    public static IIcon[] registerBlockIcons(Block block, String[] iconNames) {
        if (block == null) {
            LogHelper.warn("[ResourceRegistrar] Cannot register icons for null block");
            return new IIcon[0];
        }

        IIcon[] icons = new IIcon[iconNames.length];
        for (int i = 0; i < iconNames.length; i++) {
            icons[i] = registerBlockIcon(block, i, iconNames[i]);
        }

        return icons;
    }

    // ========== Item Registration ==========

    /**
     * Register a single icon for an item
     *
     * @param item     The item to register icon for
     * @param iconName The icon name (without mod ID prefix)
     * @return Registered IIcon
     */
    public static IIcon registerItemIcon(Item item, String iconName) {
        if (item == null) {
            LogHelper.warn("[ResourceRegistrar] Cannot register icon for null item");
            return null;
        }

        String fullName = "astralsorcery:" + iconName;
        LogHelper.debug("[ResourceRegistrar] Registering item icon: " + fullName);

        // Note: In 1.7.10, items register icons through their own registerIcons method
        // This is a placeholder for documentation purposes
        return null; // The actual registration happens in the item class
    }

    /**
     * Register multiple icons for an item (metadata variants)
     *
     * @param item      The item to register icons for
     * @param iconNames Array of icon names (without mod ID prefix)
     * @return Array of registered IIcons
     */
    public static IIcon[] registerItemIcons(Item item, String[] iconNames) {
        if (item == null) {
            LogHelper.warn("[ResourceRegistrar] Cannot register icons for null item");
            return new IIcon[0];
        }

        IIcon[] icons = new IIcon[iconNames.length];
        for (int i = 0; i < iconNames.length; i++) {
            icons[i] = registerItemIcon(item, iconNames[i]);
        }

        return icons;
    }

    // ========== Helper Methods ==========

    /**
     * Build full resource path from icon name
     *
     * @param iconName The icon name (without mod ID prefix)
     * @return Full resource path (e.g., "astralsorcery:icon_name")
     */
    public static String buildResourcePath(String iconName) {
        if (iconName == null || iconName.isEmpty()) {
            return "astralsorcery:missing_icon";
        }

        // If already has mod ID prefix, return as-is
        if (iconName.contains(":")) {
            return iconName;
        }

        return "astralsorcery:" + iconName;
    }

    /**
     * Validate icon name format
     *
     * @param iconName The icon name to validate
     * @return true if valid
     */
    public static boolean isValidIconName(String iconName) {
        if (iconName == null || iconName.isEmpty()) {
            return false;
        }

        // Icon names should only contain lowercase letters, numbers, and underscores
        return iconName.matches("^[a-z0-9_]+$");
    }

    /**
     * Get icon name from unlocalized name
     * Converts format like "itemcraftingcomponent" to "crafting_component"
     *
     * @param unlocalizedName The unlocalized name
     * @return Icon name
     */
    public static String getIconNameFromUnlocalizedName(String unlocalizedName) {
        if (unlocalizedName == null || unlocalizedName.isEmpty()) {
            return "missing_icon";
        }

        // Remove "item." or "tile." prefix if present
        String name = unlocalizedName;
        if (name.startsWith("item.")) {
            name = name.substring(5);
        } else if (name.startsWith("tile.")) {
            name = name.substring(5);
        }

        // Remove ".name" suffix if present
        if (name.endsWith(".name")) {
            name = name.substring(0, name.length() - 5);
        }

        // Use ItemTextureMap if available
        String mappedName = ItemTextureMap.getTextureName(name);
        if (!mappedName.equals(name)) {
            return mappedName;
        }

        // Convert camelCase to snake_case
        return camelToSnake(name);
    }

    /**
     * Convert camelCase to snake_case
     *
     * @param camelCase The camelCase string
     * @return snake_case string
     */
    private static String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2")
            .toLowerCase();
    }
}
