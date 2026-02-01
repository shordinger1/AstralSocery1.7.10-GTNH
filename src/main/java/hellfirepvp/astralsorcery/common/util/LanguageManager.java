/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Language manager for localization
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.IllegalFormatException;

import org.apache.commons.io.IOUtils;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Language manager for Astral Sorcery (1.7.10)
 * <p>
 * Handles loading and managing localizations using FML's LanguageRegistry system.
 * <p>
 * <b>1.7.10 LanguageRegistry API:</b>
 * <ul>
 * <li>Batch loading: {@code LanguageRegistry.instance().injectLanguage(lang, HashMap)}</li>
 * <li>Single entry (current lang): {@code instance().addStringLocalization(key, value)}</li>
 * <li>Single entry (specific lang): {@code instance().addStringLocalization(lang, key, value)}</li>
 * </ul>
 * <p>
 * <b>Language File Format:</b>
 * 
 * <pre>
 * # Comment lines start with #
 * item.celestialCrystal.name=Celestial Crystal
 * tile.celestialAltar.name=Celestial Altar
 * astralsorcery.desc.crafting=Crafted with starlight
 * </pre>
 * <p>
 * <b>Usage Example:</b>
 * 
 * <pre>
 * // 1. Language files are automatically loaded from assets/astralsorcery/lang/
 * // en_US.lang, zh_CN.lang
 *
 * // 2. Get localized string
 * String name = LanguageManager.localize("item.celestialCrystal.name");
 *
 * // 3. Add localization dynamically
 * LanguageManager.addLocalization("item.myItem.name", "My Item");
 * LanguageManager.addEnglishLocalization("item.myItem.name", "My Item");
 * LanguageManager.addLocalization("zh_CN", "item.myItem.name", "我的物品");
 *
 * // 4. Check if key is localized
 * if (LanguageManager.isLocalized("item.myItem.name")) {
 *     // Key has translation
 * }
 * </pre>
 * <p>
 * <b>Important Notes:</b>
 * <ul>
 * <li>Language files must be in UTF-8 encoding</li>
 * <li>Escape sequences: \n (newline), \t (tab), \" (quote), \\ (backslash)</li>
 * <li>Comments start with #</li>
 * <li>Empty lines are ignored</li>
 * <li>Lines use key=value format (no spaces around = recommended)</li>
 * </ul>
 */
public class LanguageManager {

    private static final String[] LANGUAGES = { "en_US", // English (US)
        "zh_CN" // Chinese (Simplified)
    };

    /**
     * Initialize language manager
     */
    public static void init() {
        LogHelper.entry("LanguageManager.init");

        // Load localizations for each language
        for (String lang : LANGUAGES) {
            loadLanguage(lang);
        }

        LogHelper.info("Loaded " + LANGUAGES.length + " language files");

        LogHelper.exit("LanguageManager.init");
    }

    /**
     * Load language file
     * <p>
     * 1.7.10 API: Uses HashMap + injectLanguage() for batch loading
     *
     * @param lang The language code (e.g., "en_US")
     */
    private static void loadLanguage(String lang) {
        String fileName = "astralsorcery/lang/" + lang + ".lang";
        InputStream stream = null;

        try {
            // Try to load from assets
            stream = LanguageManager.class.getResourceAsStream("/assets/" + fileName);

            if (stream == null) {
                LogHelper.warn("Language file not found: " + fileName);
                return;
            }

            // Read all lines
            String content = IOUtils.toString(stream);
            String[] lines = content.split("\n");

            // Collect all translations into HashMap
            HashMap<String, String> translations = new HashMap<>();
            int count = 0;

            for (String line : lines) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parse key=value
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx)
                        .trim();
                    String value = line.substring(idx + 1)
                        .trim();

                    // Convert escape sequences
                    value = convertEscapeSequences(value);

                    // Add to translations map
                    translations.put(key, value);
                    count++;
                }
            }

            // Batch inject all translations for this language
            if (!translations.isEmpty()) {
                LanguageRegistry.instance()
                    .injectLanguage(lang, translations);
                LogHelper.debug("Loaded " + count + " translations for " + lang);
            }

        } catch (IOException e) {
            LogHelper.error("Failed to load language file: " + fileName, e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * Convert escape sequences in localization strings
     *
     * @param value The string to convert
     * @return The converted string
     */
    private static String convertEscapeSequences(String value) {
        // Convert escape sequences
        value = value.replace("\\n", "\n");
        value = value.replace("\\t", "\t");
        value = value.replace("\\\"", "\"");
        value = value.replace("\\\\", "\\");
        return value;
    }

    /**
     * Add a localization dynamically (for current game language)
     * <p>
     * 1.7.10 API: Uses addStringLocalization() without lang parameter
     *
     * @param key   The localization key
     * @param value The localized value
     */
    public static void addLocalization(String key, String value) {
        LanguageRegistry.instance()
            .addStringLocalization(key, value);
    }

    /**
     * Add a localization for a specific language
     * <p>
     * 1.7.10 API: Uses addStringLocalization() with lang parameter
     *
     * @param lang  The language code (e.g., "en_US")
     * @param key   The localization key
     * @param value The localized value
     */
    public static void addLocalization(String lang, String key, String value) {
        LanguageRegistry.instance()
            .addStringLocalization(lang, key, value);
    }

    /**
     * Add a localization for English
     * <p>
     * 1.7.10 API: Uses addStringLocalization() with "en_US"
     *
     * @param key   The localization key
     * @param value The English value
     */
    public static void addEnglishLocalization(String key, String value) {
        LanguageRegistry.instance()
            .addStringLocalization("en_US", key, value);
    }

    /**
     * Get a localized string
     *
     * @param key  The localization key
     * @param args Optional format arguments
     * @return The localized string
     */
    public static String localize(String key, Object... args) {
        String localized = net.minecraft.util.StatCollector.translateToLocal(key);

        if (args.length > 0) {
            try {
                return String.format(localized, args);
            } catch (IllegalFormatException e) {
                LogHelper.error("Invalid format for key: " + key, e);
                return localized;
            }
        }

        return localized;
    }

    /**
     * Check if a key has a localization
     *
     * @param key The key to check
     * @return True if the key is localized
     */
    public static boolean isLocalized(String key) {
        String localized = net.minecraft.util.StatCollector.translateToLocal(key);
        return !localized.equals(key)
            || !localized.startsWith("tile.") && !localized.startsWith("item.") && !localized.startsWith("entity.");
    }

    /**
     * Get a localized item name
     *
     * @param itemName The item name
     * @return The localized name
     */
    public static String getItemName(String itemName) {
        return localize("item." + itemName + ".name");
    }

    /**
     * Get a localized block name
     *
     * @param blockName The block name
     * @return The localized name
     */
    public static String getBlockName(String blockName) {
        return localize("tile." + blockName + ".name");
    }

    /**
     * Get a localized entity name
     *
     * @param entityName The entity name
     * @return The localized name
     */
    public static String getEntityName(String entityName) {
        return localize("entity." + entityName + ".name");
    }
}
