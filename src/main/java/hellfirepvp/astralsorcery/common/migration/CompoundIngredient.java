/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Compound Ingredient that combines multiple ingredients
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * Ingredient that combines multiple ingredients with OR logic
 */
public class CompoundIngredient extends Ingredient {

    private final List<Ingredient> children;
    private ItemStack[] matchingStacks;

    public CompoundIngredient(Ingredient... children) {
        this(Arrays.asList(children));
    }

    public CompoundIngredient(Collection<Ingredient> children) {
        this.children = new ArrayList<>(children);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        if (this.matchingStacks == null) {
            ArrayList<ItemStack> stacks = new ArrayList<>();
            for (Ingredient child : this.children) {
                Collections.addAll(stacks, child.getMatchingStacks());
            }
            this.matchingStacks = stacks.toArray(new ItemStack[0]);
        }
        return this.matchingStacks;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null || input.stackSize <= 0) {
            return false;
        }

        for (Ingredient child : this.children) {
            if (child.apply(input)) {
                return true;
            }
        }
        return false;
    }

    public List<Ingredient> getChildren() {
        return Collections.unmodifiableList(this.children);
    }
}
