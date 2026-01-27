/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Unmodifiable;

import com.google.common.collect.ImmutableList;

/**
 * Astral Sorcery Recipe Map - GTNH style recipe registry
 * Manages lists of recipes for a specific recipe type
 */
public class ASRecipeMap {

    /**
     * All registered recipe maps
     */
    public static final List<ASRecipeMap> ALL_RECIPE_MAPS = new LinkedList<>();

    private final String unlocalizedName;
    private final ASRecipe.Type recipeType;
    private final List<ASRecipe> recipes = new LinkedList<>();
    private final int maxInputs;

    /**
     * Creates a new recipe map
     *
     * @param unlocalizedName Unique identifier for this recipe map
     * @param recipeType      The type of recipe this map handles
     * @param maxInputs       Maximum number of input slots for this recipe type
     */
    public ASRecipeMap(String unlocalizedName, ASRecipe.Type recipeType, int maxInputs) {
        this.unlocalizedName = unlocalizedName;
        this.recipeType = recipeType;
        this.maxInputs = maxInputs;

        if (ALL_RECIPE_MAPS.stream()
            .anyMatch(m -> m.unlocalizedName.equals(unlocalizedName))) {
            throw new IllegalArgumentException(
                "Cannot register recipe map with duplicated unlocalized name: " + unlocalizedName);
        }
        ALL_RECIPE_MAPS.add(this);
    }

    /**
     * @return The unlocalized name of this recipe map
     */
    @Nonnull
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    /**
     * @return The type of recipe this map handles
     */
    @Nonnull
    public ASRecipe.Type getRecipeType() {
        return recipeType;
    }

    /**
     * @return Maximum number of input slots for this recipe type
     */
    public int getMaxInputs() {
        return maxInputs;
    }

    /**
     * Adds a recipe to this map
     *
     * @param recipe The recipe to add
     * @return The added recipe
     */
    @Nonnull
    public ASRecipe addRecipe(@Nonnull ASRecipe recipe) {
        if (recipe.getType() != recipeType) {
            throw new IllegalArgumentException(
                "Recipe type mismatch! Expected: " + recipeType + ", got: " + recipe.getType());
        }
        recipes.add(recipe);
        return recipe;
    }

    /**
     * @return An unmodifiable view of all recipes in this map
     */
    @Unmodifiable
    public Collection<ASRecipe> getAllRecipes() {
        return ImmutableList.copyOf(recipes);
    }

    /**
     * @return The number of recipes in this map
     */
    public int getRecipeCount() {
        return recipes.size();
    }

    /**
     * Finds recipes matching the given output item stack
     *
     * @param output The output item stack to match
     * @return List of matching recipes
     */
    @Nonnull
    public List<ASRecipe> findRecipesByOutput(@Nullable ItemStack output) {
        List<ASRecipe> matches = new ArrayList<>();
        if (output == null) {
            return matches;
        }

        for (ASRecipe recipe : recipes) {
            ItemStack recipeOutput = recipe.getOutput();
            if (recipeOutput != null && ItemStack.areItemStacksEqual(recipeOutput, output)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /**
     * Finds recipes matching the given input item stacks
     *
     * @param inputs The input item stacks to match
     * @return List of matching recipes, or empty list if no matches found
     */
    @Nonnull
    public List<ASRecipe> findRecipesByInput(@Nullable ItemStack... inputs) {
        List<ASRecipe> matches = new ArrayList<>();
        if (inputs == null || inputs.length == 0) {
            return matches;
        }

        outer: for (ASRecipe recipe : recipes) {
            ItemHandle[] recipeInputs = recipe.getInputs();
            if (recipeInputs.length != inputs.length) {
                continue;
            }

            for (int i = 0; i < inputs.length; i++) {
                if (!recipeInputs[i].matchCrafting(inputs[i])) {
                    continue outer;
                }
            }
            matches.add(recipe);
        }
        return matches;
    }

    /**
     * Removes all recipes from this map
     */
    public void clear() {
        recipes.clear();
    }

    /**
     * Removes recipes that produce the given output
     *
     * @param output The output to match
     * @return Number of recipes removed
     */
    public int removeRecipesByOutput(@Nullable ItemStack output) {
        if (output == null) {
            return 0;
        }

        AtomicInteger count = new AtomicInteger();
        recipes.removeIf(recipe -> {
            ItemStack recipeOutput = recipe.getOutput();
            if (recipeOutput != null && ItemStack.areItemStacksEqual(recipeOutput, output)) {
                count.getAndIncrement();
                return true;
            }
            return false;
        });
        return count.get();
    }

    /**
     * Removes a specific recipe from this map
     *
     * @param recipe The recipe to remove
     * @return true if the recipe was removed, false if it wasn't found
     */
    public boolean removeRecipe(@Nonnull ASRecipe recipe) {
        return recipes.remove(recipe);
    }

    /**
     * Checks if this map contains a recipe that produces the given output
     *
     * @param output The output to check
     * @return true if a matching recipe exists
     */
    public boolean containsOutput(@Nullable ItemStack output) {
        if (output == null) {
            return false;
        }

        return recipes.stream()
            .anyMatch(recipe -> {
                ItemStack recipeOutput = recipe.getOutput();
                return recipeOutput != null && ItemStack.areItemStacksEqual(recipeOutput, output);
            });
    }

    /**
     * Gets all unique outputs from recipes in this map
     *
     * @return List of unique output item stacks
     */
    @Nonnull
    public List<ItemStack> getAllOutputs() {
        List<ItemStack> outputs = new ArrayList<>();
        for (ASRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput();
            if (output != null && !outputs.contains(output)) {
                outputs.add(output);
            }
        }
        return outputs;
    }
}
