/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.item.ItemStack;

/**
 * Migration class for 1.12.2 IItemColor interface
 * In 1.7.10, item colors are handled differently through IItemRenderer
 */
@FunctionalInterface
public interface IItemColor {

    /**
     * Returns the color multiplier for the item stack at the given tint index
     * 
     * @param stack     The item stack to color
     * @param tintIndex The tint index (layer)
     * @return The color multiplier, or -1 for no color change
     */
    int colorMultiplier(ItemStack stack, int tintIndex);
}
