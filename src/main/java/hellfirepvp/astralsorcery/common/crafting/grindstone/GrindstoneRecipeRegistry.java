/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Grindstone Recipe Registry - Manages grindstone recipes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;

/**
 * GrindstoneRecipeRegistry - Grindstone recipe registry (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Recipe registration and lookup</li>
 * <li>Recipe matching by input</li>
 * <li>Random recipe selection for multiple matches</li>
 * </ul>
 */
public class GrindstoneRecipeRegistry {

    private static final Random rand = new Random();

    /** All registered recipes */
    public static List<GrindstoneRecipe> recipes = new LinkedList<>();

    /**
     * Register a grindstone recipe
     *
     * @param in     Input item
     * @param out    Output item
     * @param chance 1 in X chance
     * @return Registered recipe
     */
    public static GrindstoneRecipe registerGrindstoneRecipe(ItemStack in, ItemStack out, int chance) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(in, out, chance);
        recipes.add(recipe);
        return recipe;
    }

    /**
     * Register a grindstone recipe with double output chance
     *
     * @param in           Input item
     * @param out          Output item
     * @param chance       1 in X chance
     * @param doubleChance Chance (0-1) to double output
     * @return Registered recipe
     */
    public static GrindstoneRecipe registerGrindstoneRecipe(ItemStack in, ItemStack out, int chance,
        float doubleChance) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(in, out, chance, doubleChance);
        recipes.add(recipe);
        return recipe;
    }

    /**
     * Register a grindstone recipe object
     *
     * @param recipe Recipe to register
     * @return Registered recipe
     */
    public static GrindstoneRecipe registerGrindstoneRecipe(GrindstoneRecipe recipe) {
        recipes.add(recipe);
        return recipe;
    }

    /**
     * Find a matching recipe for the input stack
     *
     * @param stackIn Input stack
     * @return Matching recipe, or null if none found
     */
    public static GrindstoneRecipe findMatchingRecipe(ItemStack stackIn) {
        List<GrindstoneRecipe> matching = new LinkedList<>();

        // Find all matching recipes
        for (GrindstoneRecipe gr : recipes) {
            if (gr.isValid() && gr.matches(stackIn)) {
                matching.add(gr);
            }
        }

        // Return random matching recipe
        if (matching.isEmpty()) {
            return null;
        }

        return matching.get(rand.nextInt(matching.size()));
    }

    /**
     * Remove a recipe by output
     *
     * @param matchOut Output to match
     * @return Removed recipe, or null if none found
     */
    public static GrindstoneRecipe tryRemoveGrindstoneRecipe(ItemStack matchOut) {
        for (GrindstoneRecipe gr : recipes) {
            if (gr.isValid()) {
                ItemStack output = gr.getOutputForMatching();
                if (output != null && matchOut != null
                    && output.getItem() == matchOut.getItem()
                    && output.getItemDamage() == matchOut.getItemDamage()) {
                    recipes.remove(gr);
                    return gr;
                }
            }
        }
        return null;
    }

    /**
     * Get all valid recipes
     *
     * @return List of valid recipes
     */
    public static List<GrindstoneRecipe> getValidRecipes() {
        List<GrindstoneRecipe> valid = new LinkedList<>();
        for (GrindstoneRecipe gr : recipes) {
            if (gr.isValid()) {
                valid.add(gr);
            }
        }
        return valid;
    }

    /**
     * Clear all recipes
     */
    public static void clearRecipes() {
        recipes.clear();
    }
}
