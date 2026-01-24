/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Migration class for 1.12.2 ItemColors class
 * In 1.7.10, this provides a compatibility layer for item color handling
 * Actual color application is handled through IItemRenderer implementations
 */
public class ItemColors {

    private final Map<Item, IItemColor> itemColorMap = new HashMap<>();

    /**
     * Get the color multiplier for the given item stack
     * 
     * @param stack     The item stack
     * @param tintIndex The tint index
     * @return The color multiplier, or -1 if no handler registered
     */
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        IItemColor handler = itemColorMap.get(stack.getItem());
        return handler == null ? -1 : handler.colorMultiplier(stack, tintIndex);
    }

    /**
     * Register an item color handler for the given blocks
     * 
     * @param itemColor The color handler
     * @param blocksIn  The blocks to register for
     */
    public void registerItemColorHandler(IItemColor itemColor, Block... blocksIn) {
        for (Block block : blocksIn) {
            if (block == null) continue;
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                itemColorMap.put(item, itemColor);
            }
        }
    }

    /**
     * Register an item color handler for the given items
     * 
     * @param itemColor The color handler
     * @param itemsIn   The items to register for
     */
    public void registerItemColorHandler(IItemColor itemColor, Item... itemsIn) {
        for (Item item : itemsIn) {
            if (item == null) continue;
            itemColorMap.put(item, itemColor);
        }
    }

    /**
     * Get the registered color handler for an item
     * 
     * @param item The item
     * @return The color handler, or null if none registered
     */
    public IItemColor getItemColorHandler(Item item) {
        return itemColorMap.get(item);
    }
}
