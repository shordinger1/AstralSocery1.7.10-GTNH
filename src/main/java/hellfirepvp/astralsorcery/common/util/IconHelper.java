/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Icon registration helper for unified resource management
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Unified icon registration helper
 * <p>
 * This class provides standardized methods for registering icons.
 * All item and block classes should use these methods instead of
 * directly calling IIconRegister.registerIcon().
 * <p>
 * Usage in item/block classes:
 * 
 * <pre>
 * // Single icon
 * private IIcon icon;
 *
 * {@literal @}Override
 * public void registerIcons(IIconRegister register) {
 *     icon = IconHelper.registerIcon(register, "texture_name");
 * }
 *
 * // Multiple icons (metadata variants)
 * private IIcon[] icons;
 *
 * {@literal @}Override
 * public void registerIcons(IIconRegister register) {
 *     icons = IconHelper.registerIcons(register, new String[]{
 *         "texture1", "texture2", "texture3"
 *     });
 * }
 *
 * {@literal @}Override
 * public IIcon getIconFromDamage(int damage) {
 *     return IconHelper.getIcon(icons, damage);
 * }
 * </pre>
 */
@SideOnly(Side.CLIENT)
public final class IconHelper {

    private IconHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========== Single Icon Registration ==========

    /**
     * Register a single icon
     *
     * @param register The IIconRegister
     * @param iconName The icon name (without mod ID prefix)
     * @return Registered IIcon, or null if registration fails
     */
    public static IIcon registerIcon(IIconRegister register, String iconName) {
        if (register == null) {
            LogHelper.warn("[IconHelper] Cannot register icon: register is null");
            return null;
        }

        if (iconName == null || iconName.isEmpty()) {
            LogHelper.warn("[IconHelper] Cannot register icon: iconName is null or empty");
            return null;
        }

        String fullPath = "astralsorcery:" + iconName;
        IIcon icon = register.registerIcon(fullPath);

        LogHelper.debug("[IconHelper] Registered icon: " + fullPath);
        return icon;
    }

    /**
     * Register a single icon from ResourceConfig
     *
     * @param register The IIconRegister
     * @param itemName The item/block name
     * @return Registered IIcon, or null if not configured
     */
    public static IIcon registerIconFromConfig(IIconRegister register, String itemName) {
        ResourceConfig.IconConfig config = ResourceConfig.getItemIcons(itemName);
        if (config == null || config.isVariant()) {
            return null;
        }

        return registerIcon(register, config.getIcon(0));
    }

    // ========== Multiple Icon Registration ==========

    /**
     * Register multiple icons (for metadata variants)
     *
     * @param register  The IIconRegister
     * @param iconNames Array of icon names (without mod ID prefix)
     * @return Array of registered IIcons
     */
    public static IIcon[] registerIcons(IIconRegister register, String[] iconNames) {
        if (register == null) {
            LogHelper.warn("[IconHelper] Cannot register icons: register is null");
            return new IIcon[0];
        }

        if (iconNames == null || iconNames.length == 0) {
            LogHelper.warn("[IconHelper] Cannot register icons: iconNames is null or empty");
            return new IIcon[0];
        }

        IIcon[] icons = new IIcon[iconNames.length];

        for (int i = 0; i < iconNames.length; i++) {
            icons[i] = registerIcon(register, iconNames[i]);
        }

        return icons;
    }

    /**
     * Register multiple icons from ResourceConfig
     *
     * @param register The IIconRegister
     * @param itemName The item/block name
     * @return Array of registered IIcons, or empty array if not configured
     */
    public static IIcon[] registerIconsFromConfig(IIconRegister register, String itemName) {
        ResourceConfig.IconConfig config = ResourceConfig.getItemIcons(itemName);
        if (config == null) {
            LogHelper.warn("[IconHelper] No icon configuration found for: " + itemName);
            return new IIcon[0];
        }

        return registerIcons(register, config.getIcons());
    }

    // ========== Icon Retrieval Helpers ==========

    /**
     * Get icon from array with bounds checking
     *
     * @param icons The icon array
     * @param index The requested index
     * @return Icon at index, or first icon if out of bounds, or null if array is empty
     */
    public static IIcon getIcon(IIcon[] icons, int index) {
        if (icons == null || icons.length == 0) {
            return null;
        }

        if (index < 0 || index >= icons.length) {
            return icons[0]; // Default to first icon
        }

        return icons[index];
    }

    /**
     * Get icon from array with default fallback
     *
     * @param icons       The icon array
     * @param index       The requested index
     * @param defaultIcon The default icon to return if array is empty
     * @return Icon at index, or first icon if out of bounds, or defaultIcon if array is empty
     */
    public static IIcon getIcon(IIcon[] icons, int index, IIcon defaultIcon) {
        if (icons == null || icons.length == 0) {
            return defaultIcon;
        }

        if (index < 0 || index >= icons.length) {
            return icons.length > 0 ? icons[0] : defaultIcon;
        }

        return icons[index];
    }

    // ========== Validation ==========

    /**
     * Check if icon array is valid
     *
     * @param icons The icon array to check
     * @return true if array is not null and has at least one non-null icon
     */
    public static boolean isValid(IIcon[] icons) {
        if (icons == null || icons.length == 0) {
            return false;
        }

        for (IIcon icon : icons) {
            if (icon != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if icon is valid
     *
     * @param icon The icon to check
     * @return true if icon is not null
     */
    public static boolean isValid(IIcon icon) {
        return icon != null;
    }

    // ========== Block Icon Helpers ==========

    /**
     * Register block icons from ResourceConfig
     *
     * @param register  The IIconRegister
     * @param blockName The block name
     * @return Array of registered IIcons, or empty array if not configured
     */
    public static IIcon[] registerBlockIconsFromConfig(IIconRegister register, String blockName) {
        ResourceConfig.IconConfig config = ResourceConfig.getBlockIcons(blockName);
        if (config == null) {
            LogHelper.warn("[IconHelper] No icon configuration found for block: " + blockName);
            return new IIcon[0];
        }

        return registerIcons(register, config.getIcons());
    }

    /**
     * Get block icon from array with side and metadata support
     *
     * @param icons The icon array
     * @param side  The block side (usually 0-6)
     * @param meta  The block metadata
     * @return Appropriate icon, or null if array is invalid
     */
    public static IIcon getBlockIcon(IIcon[] icons, int side, int meta) {
        if (icons == null || icons.length == 0) {
            return null;
        }

        // Use metadata for icon selection
        int index = meta;

        if (index < 0 || index >= icons.length) {
            index = 0;
        }

        return icons[index];
    }
}
