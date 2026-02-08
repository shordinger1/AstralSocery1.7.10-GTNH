/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Icon registration helper for unified resource management
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    // ========== Map-based Icon Registration (TST-style) ==========

    /**
     * Register variant icons using a Map storage (TST-style approach)
     * <p>
     * This method provides a more elegant way to register icons for metadata variants
     * using a Map<Integer, IIcon> instead of an array. This makes the code more
     * maintainable and flexible.
     * <p>
     * <b>Usage:</b>
     *
     * <pre>
     * // In your block class:
     * private Map<Integer, IIcon> iconMap;
     *
     * {@literal @}Override
     * public void registerBlockIcons(IIconRegister reg) {
     *     // Method 1: Use custom naming function
     *     iconMap = IconHelper.registerVariantIconMap(
     *         reg,
     *         new int[]{0, 1, 2, 3, 4, 5, 6},
     *         meta -> "marble_" + getMarbleName(meta)
     *     );
     *
     *     // Method 2: Use simple numeric naming
     *     iconMap = IconHelper.registerVariantIconMap(
     *         reg,
     *         new int[]{0, 1, 2, 3, 4, 5, 6},
     *         meta -> "myblock/" + meta
     *     );
     * }
     *
     * {@literal @}Override
     * public IIcon getIcon(int side, int meta) {
     *     return IconHelper.getIconFromMap(iconMap, meta);
     * }
     * </pre>
     *
     * @param register   The IIconRegister
     * @param metaValues Array of metadata values to register icons for
     * @param iconPath   Function that maps metadata to icon name (without mod ID prefix)
     * @return Map of metadata to registered IIcons
     */
    public static Map<Integer, IIcon> registerVariantIconMap(IIconRegister register, int[] metaValues,
        Function<Integer, String> iconPath) {
        if (register == null) {
            LogHelper.warn("[IconHelper] Cannot register icons: register is null");
            return new HashMap<>();
        }

        if (metaValues == null || metaValues.length == 0) {
            LogHelper.warn("[IconHelper] Cannot register icons: metaValues is null or empty");
            return new HashMap<>();
        }

        if (iconPath == null) {
            LogHelper.warn("[IconHelper] Cannot register icons: iconPath function is null");
            return new HashMap<>();
        }

        Map<Integer, IIcon> iconMap = new HashMap<>();

        for (int meta : metaValues) {
            String iconName = iconPath.apply(meta);
            IIcon icon = registerIcon(register, iconName);
            iconMap.put(meta, icon);
        }

        LogHelper.debug(
            "[IconHelper] Registered " + iconMap.size()
                + " variant icons for metadata values "
                + java.util.Arrays.toString(metaValues));
        return iconMap;
    }

    /**
     * Register variant icons from String array using Map storage
     * <p>
     * Convenience method that automatically maps array indices to metadata values.
     *
     * @param register  The IIconRegister
     * @param iconNames Array of icon names (without mod ID prefix)
     * @return Map of metadata (array index) to registered IIcons
     */
    public static Map<Integer, IIcon> registerVariantIconMap(IIconRegister register, String[] iconNames) {
        if (register == null) {
            LogHelper.warn("[IconHelper] Cannot register icons: register is null");
            return new HashMap<>();
        }

        if (iconNames == null || iconNames.length == 0) {
            LogHelper.warn("[IconHelper] Cannot register icons: iconNames is null or empty");
            return new HashMap<>();
        }

        Map<Integer, IIcon> iconMap = new HashMap<>();

        for (int meta = 0; meta < iconNames.length; meta++) {
            IIcon icon = registerIcon(register, iconNames[meta]);
            iconMap.put(meta, icon);
        }

        LogHelper.debug("[IconHelper] Registered " + iconMap.size() + " variant icons from name array");
        return iconMap;
    }

    /**
     * Get icon from Map with bounds checking
     * <p>
     * Helper method for retrieving icons from a Map-based storage.
     *
     * @param iconMap The icon map
     * @param meta    The metadata value
     * @return Icon for metadata, or first icon if not found, or null if map is empty
     */
    public static IIcon getIconFromMap(Map<Integer, IIcon> iconMap, int meta) {
        if (iconMap == null || iconMap.isEmpty()) {
            return null;
        }

        // Return icon for metadata, or first icon if not found
        return iconMap.getOrDefault(
            meta,
            iconMap.values()
                .iterator()
                .next());
    }

    /**
     * Get icon from Map with default fallback
     *
     * @param iconMap     The icon map
     * @param meta        The metadata value
     * @param defaultIcon The default icon to return if map is empty or icon not found
     * @return Icon for metadata, or defaultIcon if not found
     */
    public static IIcon getIconFromMap(Map<Integer, IIcon> iconMap, int meta, IIcon defaultIcon) {
        if (iconMap == null || iconMap.isEmpty()) {
            return defaultIcon;
        }

        return iconMap.getOrDefault(meta, defaultIcon);
    }
}
