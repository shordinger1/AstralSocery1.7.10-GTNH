/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * This file provides Ingredient compatibility for 1.7.10
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Simplified Ingredient implementation for 1.7.10 compatibility.
 * Based on 1.12.2 net.minecraft.item.crafting.Ingredient
 */
public class Ingredient {

    public static final Ingredient EMPTY = new Ingredient();

    private final ItemStack[] matchingStacks;
    private ItemStack[] cachedMatchingStacks = null;

    protected Ingredient() {
        this.matchingStacks = new ItemStack[0];
    }

    protected Ingredient(ItemStack... stacks) {
        this.matchingStacks = stacks;
    }

    public static Ingredient fromItem(ItemStack... stacks) {
        return new Ingredient(stacks);
    }

    public static Ingredient fromItems(ItemStack... stacks) {
        return new Ingredient(stacks);
    }

    /**
     * Check if the given ItemStack matches this ingredient
     */
    public boolean apply(@Nullable ItemStack input) {
        if (input == null || input.stackSize <= 0) {
            return false;
        }

        for (ItemStack stack : getMatchingStacks()) {
            if (stack.getItem() == input.getItem() && (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE
                || stack.getItemDamage() == input.getItemDamage())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all matching ItemStacks for this ingredient
     */
    public ItemStack[] getMatchingStacks() {
        if (this.cachedMatchingStacks == null) {
            this.cachedMatchingStacks = new ItemStack[this.matchingStacks.length];
            for (int i = 0; i < this.matchingStacks.length; i++) {
                this.cachedMatchingStacks[i] = this.matchingStacks[i].copy();
            }
        }
        return this.cachedMatchingStacks;
    }

    /**
     * Get the matching stack count
     */
    public int getMatchingStacksCount() {
        return getMatchingStacks().length;
    }

    /**
     * Check if this ingredient has no matching stacks
     */
    public boolean hasNoMatchingStacks() {
        return getMatchingStacksCount() == 0;
    }

    /**
     * Create an Ingredient from an OreDictionary name
     */
    public static Ingredient fromOreDict(String oreName) {
        return new OreIngredient(oreName);
    }

    /**
     * Create a compound ingredient from multiple ingredients
     */
    public static Ingredient fromIngredients(Ingredient... ingredients) {
        return new CompoundIngredient(ingredients);
    }
}
