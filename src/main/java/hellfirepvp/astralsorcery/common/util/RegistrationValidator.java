/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Registration validation and logging utility
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.registry.reference.ItemsAS;
import hellfirepvp.astralsorcery.common.item.ItemTextureMap;

// 1.7.10: GameRegistry.UniqueIdentifier exists in 1.7.10
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 * Registration validation and logging utility
 * <p>
 * Provides comprehensive logging of:
 * - All registered items and their properties
 * - Missing localized names
 * - Missing texture registrations
 * - Icon registration status
 * - Runtime validation
 * <p>
 * Generates detailed log files for debugging missing translations and textures
 */
public class RegistrationValidator {

    private static final List<String> validationErrors = new ArrayList<>();
    private static final List<String> validationWarnings = new ArrayList<>();
    private static final Set<String> loggedItems = new HashSet<>();
    private static final Map<String, ItemInfo> itemInfoMap = new HashMap<>();

    /**
     * Item information for logging
     */
    public static class ItemInfo {
        public final String registryName;
        public final String unlocalizedName;
        public final String langKey;
        public final boolean hasIconRegistration;
        public final boolean hasTexture;
        public final Class<?> itemClass;

        public ItemInfo(String registryName, String unlocalizedName, String langKey,
                       boolean hasIconRegistration, boolean hasTexture, Class<?> itemClass) {
            this.registryName = registryName;
            this.unlocalizedName = unlocalizedName;
            this.langKey = langKey;
            this.hasIconRegistration = hasIconRegistration;
            this.hasTexture = hasTexture;
            this.itemClass = itemClass;
        }

        @Override
        public String toString() {
            return String.format("ItemInfo{name=%s, lang=%s, icon=%s, texture=%s, class=%s}",
                    registryName, langKey, hasIconRegistration, hasTexture, itemClass.getSimpleName());
        }
    }

    /**
     * Validate all item registrations after preInit
     * Call this from RegistryItems.preInit() after all items are registered
     */
    public static void validateRegistrations() {
        LogHelper.entry("RegistrationValidator.validateRegistrations");

        LogHelper.info("=== Astral Sorcery Item Registration Validation ===");
        LogHelper.info("");

        int totalItems = 0;
        int itemsWithLang = 0;
        int itemsWithIcons = 0;
        int itemsWithTextures = 0;

        // Validate all registered items
        for (Item item : RegistryItems.getRegisteredItems()) {
            totalItems++;

            String unlocalizedName = item.getUnlocalizedName();
            // 1.7.10: findUniqueIdentifierFor returns UniqueIdentifier, not String
            UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(item);
            String registryName = uid != null ? uid.toString() : unlocalizedName;
            String langKey = "item." + unlocalizedName + ".name";

            // Check for icon registration
            boolean hasIcon = hasIconRegistration(item);
            if (hasIcon) {
                itemsWithIcons++;
            } else {
                validationWarnings.add("MISSING ICON: " + registryName + " (" + item.getClass().getSimpleName() + ")");
            }

            // Check for texture
            boolean hasTexture = ItemTextureMap.hasTexture(unlocalizedName);
            if (hasTexture) {
                itemsWithTextures++;
            } else {
                validationWarnings.add("MISSING TEXTURE: " + registryName + " - no texture in ItemTextureMap");
            }

            // Store item info
            ItemInfo info = new ItemInfo(registryName, unlocalizedName, langKey, hasIcon, hasTexture, item.getClass());
            itemInfoMap.put(registryName, info);

            // Log item details
            LogHelper.info(String.format("✓ %-40s lang=%s icon=%s texture=%s",
                    registryName,
                    hasIcon ? "✓" : "✗",
                    hasIcon ? "✓" : "✗",
                    hasTexture ? "✓" : "✗"));
        }

        LogHelper.info("");
        LogHelper.info("=== Registration Summary ===");
        LogHelper.info("Total Items: " + totalItems);
        LogHelper.info("Items with Icon Registration: " + itemsWithIcons + " / " + totalItems);
        LogHelper.info("Items with Texture Mapping: " + itemsWithTextures + " / " + totalItems);
        LogHelper.info("");

        // Log warnings
        if (!validationWarnings.isEmpty()) {
            LogHelper.warn("=== Validation Warnings (" + validationWarnings.size() + ") ===");
            for (String warning : validationWarnings) {
                LogHelper.warn("  " + warning);
            }
            LogHelper.info("");
        }

        // Generate log file
        generateValidationLog();

        LogHelper.exit("RegistrationValidator.validateRegistrations");
    }

    /**
     * Check if an item has icon registration
     */
    private static boolean hasIconRegistration(Item item) {
        try {
            // Check if item has registerIcons method
            java.lang.reflect.Method method = item.getClass().getMethod("registerIcons",
                    net.minecraft.client.renderer.texture.IIconRegister.class);
            return method != null && method.getDeclaringClass() != Item.class;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Generate detailed validation log file
     */
    private static void generateValidationLog() {
        FileWriter writer = null;
        try {
            File logDir = new File("logs/astralsorcery");
            logDir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File logFile = new File(logDir, "registration_validation_" + timestamp + ".log");

            writer = new FileWriter(logFile);
            PrintWriter pw = new PrintWriter(writer);

            pw.println("================================================================================");
            pw.println("   Astral Sorcery - Item Registration Validation Log");
            pw.println("   Generated: " + new Date());
            pw.println("================================================================================");
            pw.println();

            // Summary
            pw.println("SUMMARY");
            pw.println("-------");
            pw.println("Total Items Registered: " + itemInfoMap.size());
            pw.println("Items with Icon Registration: " + itemInfoMap.values().stream().filter(i -> i.hasIconRegistration).count());
            pw.println("Items with Texture Mapping: " + itemInfoMap.values().stream().filter(i -> i.hasTexture).count());
            pw.println();

            // Item details
            pw.println("ITEM DETAILS");
            pw.println("------------");
            for (Map.Entry<String, ItemInfo> entry : itemInfoMap.entrySet()) {
                ItemInfo info = entry.getValue();
                pw.println("Registry Name: " + info.registryName);
                pw.println("  Unlocalized Name: " + info.unlocalizedName);
                pw.println("  Lang Key: " + info.langKey);
                pw.println("  Icon Registration: " + (info.hasIconRegistration ? "YES" : "NO"));
                pw.println("  Texture Mapping: " + (info.hasTexture ? "YES" : "NO"));
                pw.println("  Item Class: " + info.itemClass.getName());
                pw.println();
            }

            // Missing icon registrations
            pw.println("MISSING ICON REGISTRATION");
            pw.println("-------------------------");
            int missingCount = 0;
            for (ItemInfo info : itemInfoMap.values()) {
                if (!info.hasIconRegistration) {
                    missingCount++;
                    pw.println(info.registryName + " (" + info.itemClass.getSimpleName() + ")");
                }
            }
            if (missingCount == 0) {
                pw.println("None - all items have icon registration!");
            }
            pw.println();

            // Missing texture mappings
            pw.println("MISSING TEXTURE MAPPING");
            pw.println("-----------------------");
            int missingTexture = 0;
            for (ItemInfo info : itemInfoMap.values()) {
                if (!info.hasTexture) {
                    missingTexture++;
                    pw.println(info.registryName + " - needs texture in ItemTextureMap");
                }
            }
            if (missingTexture == 0) {
                pw.println("None - all items have texture mapping!");
            }
            pw.println();

            // Language file entries needed
            pw.println("REQUIRED LANGUAGE FILE ENTRIES");
            pw.println("------------------------------");
            pw.println("Add these entries to: src/main/resources/assets/astralsorcery/lang/en_us.lang");
            pw.println();
            for (ItemInfo info : itemInfoMap.values()) {
                pw.println("# " + info.itemClass.getSimpleName());
                pw.println("item." + info.unlocalizedName + ".name=" + toTitleCase(info.registryName));
                pw.println();
            }

            pw.println("================================================================================");
            pw.println("END OF VALIDATION LOG");
            pw.println("================================================================================");

            pw.flush();
            LogHelper.info("Validation log saved to: " + logFile.getAbsolutePath());

        } catch (IOException e) {
            LogHelper.error("Failed to write validation log", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Convert registry name to title case
     */
    private static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String[] words = input.replace("item.", "").replace("_", " ").split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }

        return result.toString().trim();
    }

    /**
     * Log runtime item display names for validation
     * Call this during game to check what items actually show as
     */
    public static void logItemDisplayNames() {
        LogHelper.info("=== Runtime Item Display Name Check ===");

        for (Item item : RegistryItems.getRegisteredItems()) {
            ItemStack stack = new ItemStack(item, 1, 0);
            String displayName = stack.getDisplayName();
            String unlocalizedName = item.getUnlocalizedName();

            // If display name shows unlocalized name, translation is missing
            if (displayName.startsWith("item.")) {
                LogHelper.warn("MISSING TRANSLATION: " + unlocalizedName + " -> " + displayName);
            } else {
                LogHelper.info("✓ " + unlocalizedName + " -> " + displayName);
            }
        }
    }

    /**
     * Generate a template for missing language entries
     */
    public static void generateLanguageTemplate() {
        FileWriter writer = null;
        try {
            File file = new File("src/main/resources/assets/astralsorcery/lang/en_us.lang");
            file.getParentFile().mkdirs();

            boolean exists = file.exists();
            writer = new FileWriter(file, true); // Append mode
            PrintWriter pw = new PrintWriter(writer);

            if (!exists) {
                pw.println("# Astral Sorcery Language File");
                pw.println("# Auto-generated by RegistrationValidator");
                pw.println();
            }

            pw.println("# =============================================================================");
            pw.println("# Auto-generated Item Translations");
            pw.println("# Generated: " + new Date());
            pw.println("# =============================================================================");
            pw.println();

            for (ItemInfo info : itemInfoMap.values()) {
                String entry = "item." + info.unlocalizedName + ".name=" + toTitleCase(info.registryName);
                pw.println(entry);
            }

            pw.println();
            pw.flush();

            LogHelper.info("Language template appended to: " + file.getAbsolutePath());

        } catch (IOException e) {
            LogHelper.error("Failed to generate language template", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private RegistrationValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
