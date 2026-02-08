/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Localization helper for Items and Blocks that don't extend base classes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Localization helper for Items and Blocks
 * <p>
 * Provides localization utility methods for items and blocks that don't extend
 * AstralBaseItem or AstralBaseBlock. This includes:
 * <ul>
 * <li>Items that extend Minecraft's ItemTool, ItemArmor, ItemBow, etc.</li>
 * <li>Blocks that extend special Minecraft classes</li>
 * <li>Any class that needs localization but can't extend our base classes</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 *
 * <pre>
 * public class ItemCrystalPickaxe extends ItemTool {
 *
 *     public ItemCrystalPickaxe() {
 *         super(ToolMaterial.EMERALD);
 *     }
 *
 *     &#64;Override
 *     public void addInformation(ItemStack stack, EntityPlayer player, List&lt;String&gt; tooltip, boolean advanced) {
 *         // Add tooltip lines from localization
 *         LocalizationHelper.addItemTooltip(stack, tooltip, 3, true);
 *     }
 * }
 * </pre>
 *
 * @author Astral Sorcery Team
 * @version 1.7.10
 */
public class LocalizationHelper {

    private LocalizationHelper() {
        // Private constructor
    }

    // ========================================================================
    // Item Localization
    // ========================================================================

    /**
     * Get the localized name for an item.
     *
     * @param item The item
     * @return The localized display name
     */
    public static String getItemName(Item item) {
        if (item == null) {
            return "";
        }
        String key = item.getUnlocalizedName() + ".name";
        return ASUtils.tr(key);
    }

    /**
     * Get the localized name for an item stack.
     *
     * @param stack The item stack
     * @return The localized display name
     */
    public static String getItemStackName(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return "";
        }
        return stack.getItem()
            .getItemStackDisplayName(stack);
    }

    /**
     * Get a specific tooltip line for an item.
     *
     * @param item The item
     * @param line The tooltip line number (1-based)
     * @return The localized tooltip line
     */
    public static String getItemTooltip(Item item, int line) {
        if (item == null) {
            return "";
        }
        String baseKey = item.getUnlocalizedName();
        // Remove "item." prefix if present
        if (baseKey.startsWith("item.")) {
            baseKey = baseKey.substring(5);
        }
        String key = baseKey + ".tooltip." + line;
        return ASUtils.tr(key);
    }

    /**
     * Get a specific tooltip line for an item stack.
     *
     * @param stack The item stack
     * @param line  The tooltip line number (1-based)
     * @return The localized tooltip line
     */
    public static String getItemTooltip(ItemStack stack, int line) {
        if (stack == null || stack.getItem() == null) {
            return "";
        }
        return getItemTooltip(stack.getItem(), line);
    }

    /**
     * Add multiple tooltip lines to an item stack.
     * <p>
     * Automatically checks for existence of each tooltip line and only adds
     * lines that have translations.
     *
     * @param stack     The item stack
     * @param tooltip   The tooltip list to add to
     * @param lineCount Number of tooltip lines to add
     * @param shiftOnly If true, only show when shift is pressed
     */
    @SideOnly(Side.CLIENT)
    public static void addItemTooltip(ItemStack stack, List<String> tooltip, int lineCount, boolean shiftOnly) {
        if (stack == null || stack.getItem() == null) {
            return;
        }

        // Check shift requirement
        if (shiftOnly && !isShiftKeyDown()) {
            tooltip.add(ASUtils.tr("misc.moreInformation"));
            return;
        }

        Item item = stack.getItem();
        // Add each tooltip line if it exists
        for (int i = 1; i <= lineCount; i++) {
            String tip = getItemTooltip(item, i);
            String key = item.getUnlocalizedName();
            if (key.startsWith("item.")) {
                key = key.substring(5);
            }
            String expectedKey = key + ".tooltip." + i;
            // Only add if the translation exists (not equal to the key itself)
            if (!tip.equals(expectedKey)) {
                tooltip.add(tip);
            }
        }
    }

    /**
     * Add multiple tooltip lines to an item stack (always show).
     *
     * @param stack     The item stack
     * @param tooltip   The tooltip list to add to
     * @param lineCount Number of tooltip lines to add
     */
    @SideOnly(Side.CLIENT)
    public static void addItemTooltip(ItemStack stack, List<String> tooltip, int lineCount) {
        addItemTooltip(stack, tooltip, lineCount, false);
    }

    // ========================================================================
    // Block Localization
    // ========================================================================

    /**
     * Get the localized name for a block.
     *
     * @param block The block
     * @return The localized display name
     */
    public static String getBlockName(Block block) {
        if (block == null) {
            return "";
        }
        String key = block.getUnlocalizedName() + ".name";
        return ASUtils.tr(key);
    }

    /**
     * Get a specific tooltip line for a block (from its item form).
     *
     * @param block The block
     * @param line  The tooltip line number (1-based)
     * @return The localized tooltip line
     */
    public static String getBlockTooltip(Block block, int line) {
        if (block == null) {
            return "";
        }
        String baseKey = block.getUnlocalizedName();
        // Remove "tile." prefix if present
        if (baseKey.startsWith("tile.")) {
            baseKey = baseKey.substring(5);
        }
        String key = baseKey + ".tooltip." + line;
        return ASUtils.tr(key);
    }

    /**
     * Add multiple tooltip lines to a block (from its item form).
     *
     * @param block     The block
     * @param tooltip   The tooltip list to add to
     * @param lineCount Number of tooltip lines to add
     * @param shiftOnly If true, only show when shift is pressed
     */
    @SideOnly(Side.CLIENT)
    public static void addBlockTooltip(Block block, List<String> tooltip, int lineCount, boolean shiftOnly) {
        if (block == null) {
            return;
        }

        // Check shift requirement
        if (shiftOnly && !isShiftKeyDown()) {
            tooltip.add(ASUtils.tr("misc.moreInformation"));
            return;
        }

        // Add each tooltip line if it exists
        for (int i = 1; i <= lineCount; i++) {
            String tip = getBlockTooltip(block, i);
            String key = block.getUnlocalizedName();
            if (key.startsWith("tile.")) {
                key = key.substring(5);
            }
            String expectedKey = key + ".tooltip." + i;
            // Only add if the translation exists
            if (!tip.equals(expectedKey)) {
                tooltip.add(tip);
            }
        }
    }

    /**
     * Add multiple tooltip lines to a block (always show).
     *
     * @param block     The block
     * @param tooltip   The tooltip list to add to
     * @param lineCount Number of tooltip lines to add
     */
    @SideOnly(Side.CLIENT)
    public static void addBlockTooltip(Block block, List<String> tooltip, int lineCount) {
        addBlockTooltip(block, tooltip, lineCount, false);
    }

    // ========================================================================
    // Utility Methods
    // ========================================================================

    /**
     * Check if player is holding shift key (client-side).
     *
     * @return true if shift is pressed
     */
    @SideOnly(Side.CLIENT)
    public static boolean isShiftKeyDown() {
        return net.minecraft.client.gui.GuiScreen.isShiftKeyDown();
    }

    /**
     * Check if a localization key exists.
     *
     * @param key The localization key
     * @return true if the key has a translation
     */
    public static boolean hasTranslation(String key) {
        return ASUtils.canTranslate(key);
    }

    /**
     * Get localized text by key.
     *
     * @param key The localization key
     * @return The localized text
     */
    public static String tr(String key) {
        return ASUtils.tr(key);
    }

    /**
     * Get localized text with formatting.
     *
     * @param key  The localization key
     * @param args Format arguments
     * @return The localized and formatted text
     */
    public static String tr(String key, Object... args) {
        return ASUtils.tr(key, args);
    }

    // ========================================================================
    // Debug/Logging Methods
    // ========================================================================

    /**
     * Log the localization information for an item.
     *
     * @param item The item to log info for
     */
    public static void logItemInfo(Item item) {
        if (item == null) {
            LogHelper.warn("[LocalizationHelper] Attempted to log null item!");
            return;
        }

        String unlocalizedName = item.getUnlocalizedName();
        String localizationKey = unlocalizedName + ".name";
        String localizedName = ASUtils.tr(localizationKey);

        LogHelper.info(
            "[Item Localization] " + unlocalizedName
                + "\n"
                + "  - localizationKey: "
                + localizationKey
                + "\n"
                + "  - localizedName: "
                + localizedName);
    }

    /**
     * Log the localization information for a block.
     *
     * @param block The block to log info for
     */
    public static void logBlockInfo(Block block) {
        if (block == null) {
            LogHelper.warn("[LocalizationHelper] Attempted to log null block!");
            return;
        }

        String unlocalizedName = block.getUnlocalizedName();
        String localizationKey = unlocalizedName + ".name";
        String localizedName = ASUtils.tr(localizationKey);

        LogHelper.info(
            "[Block Localization] " + unlocalizedName
                + "\n"
                + "  - localizationKey: "
                + localizationKey
                + "\n"
                + "  - localizedName: "
                + localizedName);
    }
}
