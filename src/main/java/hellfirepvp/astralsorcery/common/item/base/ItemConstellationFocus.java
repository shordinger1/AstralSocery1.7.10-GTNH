/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation Focus - Interface for constellation-linked items
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.item.ItemStack;

/**
 * Interface for items that are linked to a constellation
 * <p>
 * Used by tuned crystals and other constellation-focused items
 * to provide their associated constellation.
 */
public interface ItemConstellationFocus {

    /**
     * Get the constellation this item is focused on
     * @param stack The item stack
     * @return The constellation, or null if none
     */
    IConstellation getFocusConstellation(ItemStack stack);
}
