/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Property Item - Interface for items with crystal properties
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import net.minecraft.item.ItemStack;

/**
 * Interface for items that hold crystal properties
 * <p>
 * Items implementing this interface can store and retrieve
 * crystal properties (size, purity, fracture, etc.) from NBT.
 */
public interface CrystalPropertyItem {

    /**
     * Get the maximum crystal size for this item type
     * 
     * @param stack The item stack
     * @return Maximum size (e.g., 300 for rock crystals, 900 for celestial)
     */
    int getMaxSize(ItemStack stack);

    /**
     * Provide current crystal properties
     * 
     * @param stack The item stack
     * @return Crystal properties, or null if not set
     */
    CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack);
}
