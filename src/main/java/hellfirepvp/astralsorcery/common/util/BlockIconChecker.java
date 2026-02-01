/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Utility class to check blockIcon status for debugging
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Helper class to check block icon status for debugging
 * <p>
 * Provides static methods to verify if block icons are properly registered.
 */
@SideOnly(Side.CLIENT)
public final class BlockIconChecker {

    /**
     * Get the icon from a block instance
     *
     * @param block The block to check
     * @return The block icon, or null if not registered
     */
    public static IIcon getBlockIcon(Block block) {
        if (block == null) {
            return null;
        }
        try {
            return block.getIcon(0, 0);
        } catch (Exception e) {
            LogHelper.warn(
                "[BlockIconCheck] Exception getting icon for " + block.getUnlocalizedName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a block's icon is null and log the result
     *
     * @param block     The block to check
     * @param blockName The name of the block for logging
     * @return true if icon is null, false otherwise
     */
    public static boolean isIconNull(Block block, String blockName) {
        if (block == null) {
            LogHelper.warn("[BlockIconCheck] NULL BLOCK: " + blockName);
            return true;
        }

        IIcon icon = getBlockIcon(block);
        boolean isNull = (icon == null);

        if (isNull) {
            LogHelper.warn(
                "[BlockIconCheck] NULL ICON: " + blockName + " (unlocalizedName: " + block.getUnlocalizedName() + ")");
        } else {
            String iconName = icon.getIconName();
            LogHelper.info("[BlockIconCheck] OK: " + blockName + " -> " + iconName);
        }

        return isNull;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private BlockIconChecker() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
