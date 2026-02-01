/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASRecipeMaps - Recipe map definitions for Astral Sorcery altars
 *
 * 1.7.10: GT-style RecipeMap system instead of 1.12.2 Crafttweaker
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * Recipe map definitions for altar crafting
 * <p>
 * 1.7.10: Uses GT-style RecipeMap system
 * - DISCOVERY_RECIPES: Discovery altar recipes
 * - ATTUNEMENT_RECIPES: Attunement altar recipes
 * - CONSTELLATION_RECIPES: Constellation altar recipes
 * - TRAIT_RECIPES: Trait altar recipes
 * <p>
 * Each RecipeMap stores recipes for a specific altar level and type.
 */
public class ASRecipeMaps {

    /**
     * Discovery altar recipes (AltarLevel.DISCOVERY)
     * Basic constellation discovery recipes
     * Altar has 9 accessible slots (3x3 grid), so allow up to 9 inputs
     */
    public static final ASRecipeMap DISCOVERY_RECIPES = new ASRecipeMap(
        "as.recipe.discovery",
        TileAltar.AltarLevel.DISCOVERY,
        1, // minItemInputs
        9, // maxItemInputs (matches accessibleInventorySize)
        0, // minFluidInputs
        0, // maxFluidInputs
        1, // minItemOutputs
        1 // maxItemOutputs
    );

    /**
     * Attunement altar recipes (AltarLevel.ATTUNEMENT)
     * Focus-based attunement recipes
     * Altar has 13 accessible slots, so allow up to 13 inputs
     */
    public static final ASRecipeMap ATTUNEMENT_RECIPES = new ASRecipeMap(
        "as.recipe.attunement",
        TileAltar.AltarLevel.ATTUNEMENT,
        1, // minItemInputs
        13, // maxItemInputs (matches accessibleInventorySize)
        0, // minFluidInputs
        0, // maxFluidInputs
        1, // minItemOutputs
        1 // maxItemOutputs
    );

    /**
     * Constellation altar recipes (AltarLevel.CONSTELLATION_CRAFT)
     * Advanced constellation crafting recipes
     * Altar has 21 accessible slots, so allow up to 21 inputs
     */
    public static final ASRecipeMap CONSTELLATION_RECIPES = new ASRecipeMap(
        "as.recipe.constellation",
        TileAltar.AltarLevel.CONSTELLATION_CRAFT,
        1, // minItemInputs
        21, // maxItemInputs (matches accessibleInventorySize)
        0, // minFluidInputs
        0, // maxFluidInputs
        1, // minItemOutputs
        1 // maxItemOutputs
    );

    /**
     * Trait altar recipes (AltarLevel.TRAIT_CRAFT)
     * Trait application recipes
     * Altar has 25 accessible slots (5x5 grid!), so allow up to 25 inputs
     */
    public static final ASRecipeMap TRAIT_RECIPES = new ASRecipeMap(
        "as.recipe.trait",
        TileAltar.AltarLevel.TRAIT_CRAFT,
        1, // minItemInputs
        25, // maxItemInputs (5x5 grid - matches accessibleInventorySize)
        0, // minFluidInputs
        0, // maxFluidInputs
        1, // minItemOutputs
        1 // maxItemOutputs
    );

    /**
     * Brilliance altar recipes (AltarLevel.BRILLIANCE)
     * End-game crafting recipes
     * Altar has 25 accessible slots (5x5 grid!), so allow up to 25 inputs
     */
    public static final ASRecipeMap BRILLIANCE_RECIPES = new ASRecipeMap(
        "as.recipe.brilliance",
        TileAltar.AltarLevel.BRILLIANCE,
        1, // minItemInputs
        25, // maxItemInputs (5x5 grid - matches accessibleInventorySize)
        0, // minFluidInputs
        0, // maxFluidInputs
        1, // minItemOutputs
        1 // maxItemOutputs
    );

    private ASRecipeMaps() {
        // Private constructor to prevent instantiation
    }
}
