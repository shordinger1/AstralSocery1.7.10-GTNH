/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Utility class for common operations including localization
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.util.StatCollector;

/**
 * Astral Sorcery Utilities
 * <p>
 * Provides common utility methods used throughout the mod.
 * <p>
 * <b>Localization Methods:</b>
 * <ul>
 * <li>{@link #tr(String)} - Get localized string by key</li>
 * <li>{@link #tr(String, Object...)} - Get localized string with formatting</li>
 * </ul>
 * <p>
 * <b>Usage Examples:</b>
 *
 * <pre>
 * // Simple localization
 * String name = ASUtils.tr("item.celestialCrystal.name");
 *
 * // With formatting
 * String message = ASUtils.tr("astralsorcery.message.welcome", playerName);
 *
 * // Using TextEnums (recommended approach)
 * import static hellfirepvp.astralsorcery.common.util.TextEnums.*;
 *
 * public static final TextEnums MY_ITEM = new TextEnums("NameMyItem");
 * // In code:
 * String name = MY_ITEM.toString();
 * </pre>
 *
 * @author Astral Sorcery Team
 * @version 1.7.10
 */
public final class ASUtils {

    private ASUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Localize by the key using StatCollector.
     * <p>
     * If the key does not exist in both of the currently used language and fallback
     * language (English), the key itself is returned.
     * <p>
     * This method uses Minecraft's standard localization system through StatCollector.
     * Language files are loaded from {@code assets/astralsorcery/lang/} directory.
     *
     * @param key the localization key
     * @return the localized text by the key, or key if the key does not exist
     * @see StatCollector#translateToLocal(String)
     */
    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    /**
     * Localize by the key with format parameters.
     * <p>
     * Uses {@link String#format(String, Object...)} for formatting.
     *
     * @param key    the localization key
     * @param format the format arguments
     * @return the localized and formatted text
     * @see StatCollector#translateToLocalFormatted(String, Object...)
     */
    public static String tr(String key, Object... format) {
        return StatCollector.translateToLocalFormatted(key, format);
    }

    /**
     * Localize by the key with fallback.
     * <p>
     * If the key doesn't exist in current language, falls back to English.
     * If still not found, returns the fallback value.
     *
     * @param key      the localization key
     * @param fallback the fallback value if key not found
     * @return the localized text, or fallback if not found
     */
    public static String trWithFallback(String key, String fallback) {
        String localized = tr(key);
        // If the key wasn't found, StatCollector returns the key itself
        if (localized.equals(key)) {
            return fallback;
        }
        return localized;
    }

    /**
     * Check if a localization key exists.
     * <p>
     * Returns true if the key is different from its input (meaning it was translated).
     *
     * @param key the localization key to check
     * @return true if the key has a translation
     */
    public static boolean canTranslate(String key) {
        String translated = tr(key);
        return !translated.equals(key);
    }

    /**
     * Build tooltip information array.
     * <p>
     * Utility method to build structured tooltip data.
     *
     * @param builder the consumer that puts info into the list
     * @return the built array of info data
     */
    public static String[] buildInfoData(Consumer<ArrayList<String>> builder) {
        ArrayList<String> info = new ArrayList<>();
        builder.accept(info);
        return info.toArray(new String[0]);
    }

    /**
     * Format a string with color codes.
     * <p>
     * Replaces {@code &} with Minecraft's section sign (§) for color codes.
     *
     * @param text the text to format
     * @return the formatted text
     */
    public static String formatColorCodes(String text) {
        return text.replace("&", "\u00A7");
    }

    /**
     * Strip color codes from text.
     *
     * @param text the text to strip
     * @return text without color codes
     */
    public static String stripColorCodes(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u00A7' && i + 1 < text.length()) {
                i++; // Skip the color code
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
