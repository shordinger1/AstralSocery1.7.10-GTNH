/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Language manager for localization
 *
 * Refactored to follow Twist Space Technology's localization approach
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.StatCollector;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Language manager for Astral Sorcery (1.7.10)
 * <p>
 * Refactored to follow the Twist Space Technology localization approach.
 * Uses a hybrid system supporting both FML's LanguageRegistry and StatCollector.
 * <p>
 * <b>Architecture:</b>
 * <ul>
 * <li><b>Primary method</b>: Language files loaded from {@code assets/astralsorcery/lang/}</li>
 * <li><b>Runtime lookup</b>: Uses {@link StatCollector#translateToLocal(String)}</li>
 * <li><b>Type-safe access</b>: Use {@link TextEnums} enum for compile-time checking</li>
 * <li><b>Utility access</b>: Use {@link ASUtils#tr(String)} for simple localization</li>
 * </ul>
 * <p>
 * <b>Recommended Usage:</b>
 *
 * <pre>
 * // Method 1: Using TextEnums (Recommended - Type Safe)
 * import static hellfirepvp.astralsorcery.common.util.TextEnums.*;
 *
 * // In code:
 * String name = ItemGroup_AstralSorcery.toString();
 *
 * // Method 2: Using ASUtils (Simple)
 * String name = ASUtils.tr("itemGroup.astralsorcery");
 *
 * // Method 3: Using LanguageManager (Legacy)
 * String name = LanguageManager.localize("itemGroup.astralsorcery");
 * </pre>
 * <p>
 * <b>Language File Format:</b>
 *
 * <pre>
 * # Comments start with #
 * item.celestialCrystal.name=Celestial Crystal
 * tile.celestialAltar.name=Celestial Altar
 * astralsorcery.desc.crafting=Crafted with starlight
 * </pre>
 * <p>
 * <b>Important Notes:</b>
 * <ul>
 * <li>Language files must be in UTF-8 encoding</li>
 * <li>Escape sequences: \n (newline), \t (tab), \" (quote), \\ (backslash)</li>
 * <li>Forge automatically loads .lang files from assets/{modid}/lang/</li>
 * <li>This LanguageManager ensures proper initialization and provides utility methods</li>
 * </ul>
 *
 * @author Astral Sorcery Team
 * @version 1.7.10 (Refactored)
 */
public class LanguageManager {

    /**
     * Supported languages for Astral Sorcery
     */
    private static final String[] LANGUAGES = { "en_us", // English (US) - Primary/Fallback
        "zh_cn", // Chinese (Simplified)
        "ja_JP", // Japanese
        "ko_KR", // Korean
        "ru_RU", // Russian
        "it_IT", // Italian
        "zh_TW" // Chinese (Traditional)
    };

    /**
     * Initialize language manager.
     * <p>
     * In 1.7.10, Forge automatically loads .lang files from assets/{modid}/lang/ directory.
     * This method ensures proper initialization and logs the loaded languages.
     * <p>
     * The actual file loading is handled by FML's resource loading system.
     * This init method primarily serves as a synchronization point and for any
     * dynamic localization registration that needs to happen at startup.
     */
    public static void init() {
        LogHelper.entry("LanguageManager.init");

        // Note: In 1.7.10, FML automatically loads .lang files from assets/astralsorcery/lang/
        // We don't need to manually load them here, but we log what languages are supported
        StringBuilder langList = new StringBuilder();
        for (int i = 0; i < LANGUAGES.length; i++) {
            if (i > 0) langList.append(", ");
            langList.append(LANGUAGES[i]);
        }

        LogHelper.info("Localization initialized for: " + langList);
        LogHelper.info("Language files are automatically loaded by FML from assets/astralsorcery/lang/");

        // Any dynamic localization initialization can go here
        // For example, adding runtime-generated localizations

        LogHelper.exit("LanguageManager.init");
    }

    /**
     * Get localized text using StatCollector (Recommended).
     * <p>
     * This is the preferred method as it directly uses Minecraft's localization system.
     *
     * @param key the localization key
     * @return the localized string
     * @see StatCollector#translateToLocal(String)
     */
    public static String localize(String key) {
        return StatCollector.translateToLocal(key);
    }

    /**
     * Get localized text with formatting.
     *
     * @param key  the localization key
     * @param args format arguments
     * @return the localized and formatted string
     * @see StatCollector#translateToLocalFormatted(String, Object...)
     */
    public static String localizeFormatted(String key, Object... args) {
        return StatCollector.translateToLocalFormatted(key, args);
    }

    /**
     * Check if a key has a localization.
     * <p>
     * Returns true if the translated value differs from the key itself.
     *
     * @param key The key to check
     * @return True if the key is localized
     */
    public static boolean isLocalized(String key) {
        String localized = StatCollector.translateToLocal(key);
        return !localized.equals(key);
    }

    // ========================================================================
    // Legacy methods for backward compatibility
    // ========================================================================

    /**
     * Get a localized string (Legacy - use {@link #localize(String)} instead).
     *
     * @param key  the localization key
     * @param args Optional format arguments (deprecated - use localizeFormatted)
     * @return the localized string
     * @deprecated Use {@link #localize(String)} or {@link #localizeFormatted(String, Object...)}
     */
    @Deprecated
    public static String getLocalizedText(String key, Object... args) {
        if (args.length > 0) {
            return localizeFormatted(key, args);
        }
        return localize(key);
    }

    /**
     * Get a localized item name.
     *
     * @param itemName The item name (without "item." prefix and ".name" suffix)
     * @return The localized name
     */
    public static String getItemName(String itemName) {
        return localize("item." + itemName + ".name");
    }

    /**
     * Get a localized block name.
     *
     * @param blockName The block name (without "tile." prefix and ".name" suffix)
     * @return The localized name
     */
    public static String getBlockName(String blockName) {
        return localize("tile." + blockName + ".name");
    }

    /**
     * Get a localized entity name.
     *
     * @param entityName The entity name (without "entity." prefix and ".name" suffix)
     * @return The localized name
     */
    public static String getEntityName(String entityName) {
        return localize("entity." + entityName + ".name");
    }

    // ========================================================================
    // Dynamic Localization (Runtime)
    // ========================================================================

    /**
     * Add a localization dynamically (for current game language).
     * <p>
     * Use this sparingly - prefer defining translations in .lang files.
     *
     * @param key   The localization key
     * @param value The localized value
     */
    public static void addLocalization(String key, String value) {
        LanguageRegistry.instance()
            .addStringLocalization(key, value);
    }

    /**
     * Add a localization for a specific language.
     * <p>
     * Use this sparingly - prefer defining translations in .lang files.
     *
     * @param lang  The language code (e.g., "en_us")
     * @param key   The localization key
     * @param value The localized value
     */
    public static void addLocalization(String lang, String key, String value) {
        LanguageRegistry.instance()
            .addStringLocalization(lang, key, value);
    }

    /**
     * Add a localization for English (fallback language).
     *
     * @param key   The localization key
     * @param value The English value
     */
    public static void addEnglishLocalization(String key, String value) {
        addLocalization("en_us", key, value);
    }

}
