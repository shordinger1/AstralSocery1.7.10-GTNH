/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Comprehensive resource checker for blocks and items
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Comprehensive checker for block and item resources
 * <p>
 * Checks:
 * - Icon registration (null or valid)
 * - Localization (translated or fallback to unlocalized name)
 * - Reports missing resources
 */
@SideOnly(Side.CLIENT)
public final class ResourceChecker {

    /**
     * Check a block's icon and localization status
     *
     * @param block     The block to check
     * @param blockName Identifier for logging
     * @return CheckResult containing status information
     */
    public static CheckResult checkBlock(Block block, String blockName) {
        if (block == null) {
            return new CheckResult(blockName, false, false, "NULL BLOCK", "NULL");
        }

        // Check icon
        IIcon icon = block.getIcon(0, 0);
        boolean hasIcon = (icon != null);

        // Check localization
        String unlocalizedName = block.getUnlocalizedName();

        // Build localization key properly
        String localizationKey;
        if (!unlocalizedName.startsWith("tile.")) {
            localizationKey = "tile." + unlocalizedName;
        } else {
            localizationKey = unlocalizedName;
        }

        // Ensure it ends with ".name"
        if (!localizationKey.endsWith(".name")) {
            localizationKey = localizationKey + ".name";
        }

        String localized = StatCollector.translateToLocal(localizationKey);
        boolean isLocalized = !localized.equals(localizationKey);

        // Get icon name if available
        String iconName = hasIcon ? icon.getIconName() : "NULL";

        return new CheckResult(blockName, hasIcon, isLocalized, iconName, localized);
    }

    /**
     * Check an item's icon and localization status
     *
     * @param item     The item to check
     * @param itemName Identifier for logging
     * @return CheckResult containing status information
     */
    public static CheckResult checkItem(Item item, String itemName) {
        if (item == null) {
            return new CheckResult(itemName, false, false, "NULL ITEM", "NULL");
        }

        // Check icon
        IIcon icon = item.getIconFromDamage(0);
        boolean hasIcon = (icon != null);

        // Check localization
        String unlocalizedName = item.getUnlocalizedName();

        // Build localization key properly
        String localizationKey;
        if (!unlocalizedName.startsWith("item.")) {
            localizationKey = "item." + unlocalizedName;
        } else {
            localizationKey = unlocalizedName;
        }

        // Ensure it ends with ".name"
        if (!localizationKey.endsWith(".name")) {
            localizationKey = localizationKey + ".name";
        }

        String localized = StatCollector.translateToLocal(localizationKey);
        boolean isLocalized = !localized.equals(localizationKey);

        // Get icon name if available
        String iconName = hasIcon ? icon.getIconName() : "NULL";

        return new CheckResult(itemName, hasIcon, isLocalized, iconName, localized);
    }

    /**
     * Result class containing check information
     */
    public static class CheckResult {

        public final String name;
        public final boolean hasIcon;
        public final boolean isLocalized;
        public final String iconName;
        public final String localizedString;

        public CheckResult(String name, boolean hasIcon, boolean isLocalized, String iconName, String localizedString) {
            this.name = name;
            this.hasIcon = hasIcon;
            this.isLocalized = isLocalized;
            this.iconName = iconName;
            this.localizedString = localizedString;
        }

        /**
         * Format result as a log string
         */
        public String format() {
            StringBuilder sb = new StringBuilder();
            sb.append(name)
                .append(" -> ");

            if (hasIcon) {
                sb.append("[Icon: ")
                    .append(iconName)
                    .append("] ");
            } else {
                sb.append("[NO ICON] ");
            }

            if (isLocalized) {
                sb.append("[Localized: ✓ \"")
                    .append(localizedString)
                    .append("\"]");
            } else {
                sb.append("[Localized: ✗ MISSING \"")
                    .append(localizedString)
                    .append("\"]");
            }

            return sb.toString();
        }

        /**
         * Check if this result has any issues
         */
        public boolean hasIssues() {
            return !hasIcon || !isLocalized;
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ResourceChecker() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
