/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;

/**
 * Example Altar Recipes
 * Demonstrates how to add altar recipes using the GTNH-style system
 *
 * This replaces the CraftTweaker script-based approach:
 *
 * OLD (ZenScript):
 *   mods.astralsorcery.Altar.addDiscoveryAltarRecipe("recipe_name", <minecraft:gold_ingot>, 100, 200, [
 *       <minecraft:iron_ingot>, <minecraft:iron_ingot>, <minecraft:iron_ingot>,
 *       <minecraft:iron_ingot>, <minecraft:iron_ingot>, <minecraft:iron_ingot>,
 *       <minecraft:iron_ingot>, <minecraft:iron_ingot>, <minecraft:iron_ingot>
 *   ]);
 *
 * NEW (Java):
 *   ASRecipe.builder(ASRecipe.Type.ALTAR_DISCOVERY)
 *       .recipeRegistryName("recipe_name")
 *       .inputs(createInputArray())
 *       .output(new ItemStack(Items.gold_ingot))
 *       .starlightRequired(100)
 *       .duration(200)
 *       .addTo(ASRecipeMaps.ALTAR_DISCOVERY);
 */
public class AltarRecipes {

    private static final int DISCOVERY_SLOTS = 9;
    private static final int ATTUNEMENT_SLOTS = 13;
    private static final int CONSTELLATION_SLOTS = 21;
    private static final int TRAIT_SLOTS = 25;

    /**
     * Loads all altar recipes
     */
    public static void load() {
        loadDiscoveryRecipes();
        loadAttunementRecipes();
        loadConstellationRecipes();
        loadTraitRecipes();
    }

    private static void loadDiscoveryRecipes() {
        // Example: Discovery altar recipe
        // ItemHandle[] inputs = new ItemHandle[DISCOVERY_SLOTS];
        // for (int i = 0; i < DISCOVERY_SLOTS; i++) {
        //     inputs[i] = ASRecipeUtils.handle(new ItemStack(Items.iron_ingot));
        // }
        //
        // ASRecipe.builder(ASRecipe.Type.ALTAR_DISCOVERY)
        //     .recipeRegistryName("custom_discovery_recipe")
        //     .inputs(inputs)
        //     .output(new ItemStack(Items.gold_ingot))
        //     .starlightRequired(100)
        //     .duration(200)
        //     .addTo(ASRecipeMaps.ALTAR_DISCOVERY);
    }

    private static void loadAttunementRecipes() {
        // Example: Attunement altar recipe
        // ItemHandle[] inputs = new ItemHandle[ATTUNEMENT_SLOTS];
        // for (int i = 0; i < ATTUNEMENT_SLOTS; i++) {
        //     inputs[i] = ASRecipeUtils.anyCrystal();
        // }
        //
        // ASRecipe.builder(ASRecipe.Type.ALTAR_ATTUNEMENT)
        //     .recipeRegistryName("custom_attunement_recipe")
        //     .inputs(inputs)
        //     .output(new ItemStack(Items.diamond))
        //     .starlightRequired(200)
        //     .duration(300)
        //     .addTo(ASRecipeMaps.ALTAR_ATTUNEMENT);
    }

    private static void loadConstellationRecipes() {
        // Example: Constellation altar recipe
        // ItemHandle[] inputs = new ItemHandle[CONSTELLATION_SLOTS];
        // for (int i = 0; i < CONSTELLATION_SLOTS; i++) {
        //     inputs[i] = ASRecipeUtils.anyAttunedCrystal();
        // }
        //
        // ASRecipe.builder(ASRecipe.Type.ALTAR_CONSTELLATION)
        //     .recipeRegistryName("custom_constellation_recipe")
        //     .inputs(inputs)
        //     .output(new ItemStack(Items.ender_pearl))
        //     .starlightRequired(300)
        //     .duration(400)
        //     .addTo(ASRecipeMaps.ALTAR_CONSTELLATION);
    }

    private static void loadTraitRecipes() {
        // Example: Trait altar recipe with constellation focus
        // ItemHandle[] inputs = new ItemHandle[TRAIT_SLOTS];
        // for (int i = 0; i < TRAIT_SLOTS; i++) {
        //     inputs[i] = ASRecipeUtils.anyCelestialCrystal();
        // }
        //
        // ASRecipe.builder(ASRecipe.Type.ALTAR_TRAIT)
        //     .recipeRegistryName("custom_trait_recipe")
        //     .inputs(inputs)
        //     .output(new ItemStack(Items.emerald))
        //     .starlightRequired(400)
        //     .duration(500)
        //     .requiredConstellationFocus(null) // or specify a constellation
        //     .addTo(ASRecipeMaps.ALTAR_TRAIT);
    }
}
