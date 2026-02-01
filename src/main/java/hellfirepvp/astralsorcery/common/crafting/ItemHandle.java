/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Item handle for constellation signature items
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import net.minecraft.item.ItemStack;

/**
 * A handle to an item for constellation signatures
 * Used to identify items associated with constellations
 */
public class ItemHandle {

    private final ItemStack stack;

    public ItemHandle(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * Get the item stack
     *
     * @return The item stack
     */
    public ItemStack getItemStack() {
        return stack;
    }

    /**
     * Check if this handle matches the given item stack
     *
     * @param other The other item stack to compare
     * @return true if the items match
     */
    public boolean matches(ItemStack other) {
        if (other == null) return false;
        return stack.getItem() == other.getItem()
            && (!stack.getHasSubtypes() || stack.getItemDamage() == other.getItemDamage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemHandle that = (ItemHandle) o;
        return matches(that.stack);
    }

    @Override
    public int hashCode() {
        int result = stack.getItem()
            .hashCode();
        if (stack.getHasSubtypes()) {
            result = 31 * result + stack.getItemDamage();
        }
        return result;
    }

    @Override
    public String toString() {
        return "ItemHandle{" + stack.getDisplayName() + "}";
    }
}
