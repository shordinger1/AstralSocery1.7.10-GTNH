/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * OreDictionary-based Ingredient
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Ingredient that matches OreDictionary entries
 */
public class OreIngredient extends Ingredient {

    private final String oreName;

    public OreIngredient(String oreName) {
        this.oreName = oreName;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        List<ItemStack> ores = OreDictionary.getOres(this.oreName);
        List<ItemStack> expanded = new ArrayList<>();

        for (ItemStack ore : ores) {
            // Expand wildcard metadata items
            if (ore.getItemDamage() == OreDictionary.WILDCARD_VALUE && !ore.isItemStackDamageable()) {
                List<ItemStack> subItems = new ArrayList<>();
                ore.getItem()
                    .getSubItems(ore.getItem(), null, subItems);
                expanded.addAll(subItems);
            } else {
                expanded.add(ore.copy());
            }
        }

        return expanded.toArray(new ItemStack[0]);
    }

    @Override
    public boolean apply(ItemStack input) {
        if (input == null || input.stackSize <= 0) {
            return false;
        }

        int[] oreIds = OreDictionary.getOreIDs(input);
        for (int id : oreIds) {
            String name = OreDictionary.getOreName(id);
            if (name != null && name.equals(this.oreName)) {
                return true;
            }
        }
        return false;
    }
}
