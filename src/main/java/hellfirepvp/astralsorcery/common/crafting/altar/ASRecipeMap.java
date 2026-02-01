/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASRecipeMap - Simplified RecipeMap for Astral Sorcery altars
 *
 * 1.7.10: GT-style RecipeMap system
 * - Recipe storage and management
 * - Recipe lookup by input
 * - NEI integration support
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * Simplified RecipeMap for Astral Sorcery altar recipes
 * <p>
 * This is a simplified version of GT's RecipeMap, adapted for Astral Sorcery's needs.
 * It manages recipes for a specific altar level and type.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * <ul>
 * <li>Stores recipes in a list</li>
 * <li>Provides recipe lookup by input</li>
 * <li>Supports NEI display</li>
 * <li>No complex metadata system</li>
 * </ul>
 */
public class ASRecipeMap {

    private final String unlocalizedName;
    private final TileAltar.AltarLevel altarLevel;
    private final int minItemInputs;
    private final int maxItemInputs;
    private final int minFluidInputs;
    private final int maxFluidInputs;
    private final int minItemOutputs;
    private final int maxItemOutputs;

    private final List<ASAltarRecipe> recipes = new ArrayList<>();

    /**
     * Create a new RecipeMap
     *
     * @param unlocalizedName Unique name for this recipe map
     * @param altarLevel      The altar level this map is for
     * @param minItemInputs   Minimum item inputs
     * @param maxItemInputs   Maximum item inputs
     * @param minFluidInputs  Minimum fluid inputs
     * @param maxFluidInputs  Maximum fluid inputs
     * @param minItemOutputs  Minimum item outputs
     * @param maxItemOutputs  Maximum item outputs
     */
    public ASRecipeMap(String unlocalizedName, TileAltar.AltarLevel altarLevel, int minItemInputs, int maxItemInputs,
        int minFluidInputs, int maxFluidInputs, int minItemOutputs, int maxItemOutputs) {
        this.unlocalizedName = unlocalizedName;
        this.altarLevel = altarLevel;
        this.minItemInputs = minItemInputs;
        this.maxItemInputs = maxItemInputs;
        this.minFluidInputs = minFluidInputs;
        this.maxFluidInputs = maxFluidInputs;
        this.minItemOutputs = minItemOutputs;
        this.maxItemOutputs = maxItemOutputs;
    }

    /**
     * Add a recipe to this map
     *
     * @param recipe The recipe to add
     * @return true if added successfully
     */
    public boolean addRecipe(ASAltarRecipe recipe) {
        if (!isValidRecipe(recipe)) {
            return false;
        }
        recipes.add(recipe);
        return true;
    }

    /**
     * Validate if a recipe fits this RecipeMap's constraints
     */
    private boolean isValidRecipe(ASAltarRecipe recipe) {
        ItemStack[] inputs = recipe.getInputs();
        ItemStack output = recipe.getOutput();

        int inputCount = countNonEmpty(inputs);
        int outputCount = (output != null) ? 1 : 0;

        return inputCount >= minItemInputs && inputCount <= maxItemInputs
            && outputCount >= minItemOutputs
            && outputCount <= maxItemOutputs;
    }

    private int countNonEmpty(ItemStack[] stacks) {
        int count = 0;
        for (ItemStack stack : stacks) {
            if (stack != null && stack.stackSize > 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Find a recipe matching the given inputs
     *
     * @param inputs The input items
     * @return The matching recipe, or null if none found
     */
    public ASAltarRecipe findRecipe(ItemStack[] inputs) {
        for (ASAltarRecipe recipe : recipes) {
            if (recipe.matches(inputs)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Find all recipes that produce the given output
     *
     * @param output The output item
     * @return List of matching recipes
     */
    public List<ASAltarRecipe> findRecipesByOutput(ItemStack output) {
        return recipes.stream()
            .filter(recipe -> recipe.isOutput(output))
            .collect(Collectors.toList());
    }

    /**
     * Get all recipes in this map
     */
    public List<ASAltarRecipe> getAllRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    /**
     * Get the unique name of this RecipeMap
     */
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    /**
     * Get the altar level this RecipeMap is for
     */
    public TileAltar.AltarLevel getAltarLevel() {
        return altarLevel;
    }

    /**
     * Get recipe count
     */
    public int getRecipeCount() {
        return recipes.size();
    }

    /**
     * Clear all recipes
     */
    public void clearRecipes() {
        recipes.clear();
    }
}
