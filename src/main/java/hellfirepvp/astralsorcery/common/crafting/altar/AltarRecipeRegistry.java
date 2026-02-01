/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * AltarRecipeRegistry - Altar recipe registry
 *
 * 1.7.10: GT-style recipe system
 * - RecipeMap-based recipe storage
 * - No Crafttweaker integration
 * - Simplified recipe lookup
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import java.util.*;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Registry for altar recipes
 * <p>
 * Manages all altar recipes and provides lookup functionality.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * <ul>
 * <li>No Crafttweaker integration</li>
 * <li>Uses ASRecipeMap for storage</li>
 * <li>Simple recipe lookup by input/output</li>
 * <li>No advancement instances</li>
 * <li>NEI-compatible recipe structure</li>
 * </ul>
 */
public class AltarRecipeRegistry {

    private static boolean initialized = false;

    /**
     * Recipe lookup by altar level
     */
    private static final Map<TileAltar.AltarLevel, ASRecipeMap> recipeMaps = new HashMap<>();

    /**
     * All registered recipes (for NEI display)
     */
    private static final List<ASAltarRecipe> allRecipes = new ArrayList<>();

    /**
     * Initialize recipe registry with default recipe maps
     * This is called during mod initialization
     */
    public static void init() {
        if (initialized) {
            return;
        }

        LogHelper.info("Initializing AltarRecipeRegistry...");

        // Register recipe maps for each altar level
        registerRecipeMap(ASRecipeMaps.DISCOVERY_RECIPES);
        registerRecipeMap(ASRecipeMaps.ATTUNEMENT_RECIPES);
        registerRecipeMap(ASRecipeMaps.CONSTELLATION_RECIPES);
        registerRecipeMap(ASRecipeMaps.TRAIT_RECIPES);
        registerRecipeMap(ASRecipeMaps.BRILLIANCE_RECIPES);

        initialized = true;
        LogHelper.info("AltarRecipeRegistry initialized with " + recipeMaps.size() + " recipe maps");
    }

    /**
     * Register a recipe map
     */
    private static void registerRecipeMap(ASRecipeMap recipeMap) {
        recipeMaps.put(recipeMap.getAltarLevel(), recipeMap);
    }

    /**
     * Add a recipe to the registry
     *
     * @param recipe The recipe to add
     * @return true if added successfully
     */
    public static boolean addRecipe(ASAltarRecipe recipe) {
        if (!initialized) {
            LogHelper.warn("Attempted to add recipe before initialization: " + recipe);
            return false;
        }

        ASRecipeMap recipeMap = recipeMaps.get(recipe.getAltarLevel());
        if (recipeMap == null) {
            LogHelper.warn("No recipe map found for altar level: " + recipe.getAltarLevel());
            return false;
        }

        boolean added = recipeMap.addRecipe(recipe);
        if (added) {
            allRecipes.add(recipe);
            LogHelper.info(
                "Added recipe: " + recipe.getOutput()
                    .getDisplayName() + " for altar level: " + recipe.getAltarLevel());
        }
        return added;
    }

    /**
     * Find a recipe matching the given inputs for the specified altar level
     *
     * @param inputs     The input items
     * @param altarLevel The altar level
     * @return The matching recipe, or null if none found
     */
    public static ASAltarRecipe findRecipe(ItemStack[] inputs, TileAltar.AltarLevel altarLevel) {
        if (!initialized) {
            return null;
        }

        ASRecipeMap recipeMap = recipeMaps.get(altarLevel);
        if (recipeMap == null) {
            return null;
        }

        return recipeMap.findRecipe(inputs);
    }

    /**
     * Find all recipes that produce the given output
     *
     * @param output The output item
     * @return List of matching recipes
     */
    public static List<ASAltarRecipe> findRecipesByOutput(ItemStack output) {
        if (!initialized) {
            return Collections.emptyList();
        }

        List<ASAltarRecipe> results = new ArrayList<>();
        for (ASRecipeMap recipeMap : recipeMaps.values()) {
            results.addAll(recipeMap.findRecipesByOutput(output));
        }
        return results;
    }

    /**
     * Get all recipes for a specific altar level
     *
     * @param altarLevel The altar level
     * @return List of recipes
     */
    public static List<ASAltarRecipe> getRecipesForLevel(TileAltar.AltarLevel altarLevel) {
        if (!initialized) {
            System.out.println("[ASRecipeRegistry] Not initialized!");
            return Collections.emptyList();
        }

        ASRecipeMap recipeMap = recipeMaps.get(altarLevel);
        if (recipeMap == null) {
            System.out.println("[ASRecipeRegistry] No recipe map for level: " + altarLevel);
            return Collections.emptyList();
        }

        List<ASAltarRecipe> recipes = recipeMap.getAllRecipes();
        System.out.println("[ASRecipeRegistry] Returning " + recipes.size() + " recipes for level: " + altarLevel);
        return recipes;
    }

    /**
     * Get all recipes
     *
     * @return Unmodifiable list of all recipes
     */
    public static List<ASAltarRecipe> getAllRecipes() {
        return Collections.unmodifiableList(allRecipes);
    }

    /**
     * Get recipe map for a specific altar level
     *
     * @param altarLevel The altar level
     * @return The recipe map, or null if not found
     */
    public static ASRecipeMap getRecipeMap(TileAltar.AltarLevel altarLevel) {
        return recipeMaps.get(altarLevel);
    }

    /**
     * Check if registry is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Reset the registry (for testing/debugging)
     */
    public static void reset() {
        initialized = false;
        recipeMaps.clear();
        allRecipes.clear();
    }
}
