/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Highlighted item interface - Items with custom highlight colors
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import java.awt.Color;

import net.minecraft.item.ItemStack;

/**
 * ItemHighlighted - Highlighted item interface (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Interface for items that have custom highlight colors</li>
 * <li>Used by EntityItemHighlighted for rendering</li>
 * <li>Default color is white if not overridden</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Removed default method - Java 7 doesn't support interface default methods</li>
 * <li>Implementing classes must provide their own getHighlightColor() implementation</li>
 * <li>For default white color, implement as: <code>return Color.WHITE;</code></li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * 
 * <pre>
 * public class MyCrystalItem extends Item implements ItemHighlighted {
 *
 *     &#64;Override
 *     public Color getHighlightColor(ItemStack stack) {
 *         // Return custom color based on stack NBT or properties
 *         return Color.CYAN;
 *     }
 *
 *     // For default white color:
 *     // return Color.WHITE;
 * }
 * </pre>
 */
public interface ItemHighlighted {

    /**
     * Get the highlight color for this item
     * 1.7.10: Not a default method - implementing classes must override
     *
     * @param stack The item stack to get color for
     * @return The highlight color (default: Color.WHITE)
     */
    public Color getHighlightColor(ItemStack stack);

}
