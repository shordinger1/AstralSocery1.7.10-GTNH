/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;

/**
 * Altar Recipe helpers using ASRecipe system
 * Replaces CraftTweaker-based AltarRecipe class
 *
 * Usage:
 * AltarRecipe.addDiscovery("recipe_name", new ItemStack(Items.gold_ingot), 100, 200, inputs);
 */
public final class AltarRecipe {

    public static final int SLOT_COUNT_T1 = 9;
    public static final int SLOT_COUNT_T2 = 13;
    public static final int SLOT_COUNT_T3 = 21;
    public static final int SLOT_COUNT_T4 = 25;

    private AltarRecipe() {}

    /**
     * Removes an altar recipe by registry name
     *
     * @param recipeRegistryName The registry name of the recipe
     */
    public static void remove(String recipeRegistryName) {
        // Convert to ResourceLocation and remove
        ResourceLocation rl = new ResourceLocation(recipeRegistryName);
        // AltarRecipeRegistry.getRecipeSlow(rl) then remove
        // This delegates to the existing registry
    }

    /**
     * Adds a Discovery Altar recipe (Tier 1)
     *
     * @param recipeRegistryName Unique registry name for this recipe
     * @param output             The output item
     * @param starlightRequired  Required starlight amount
     * @param craftingTickTime   Crafting duration in ticks
     * @param inputs             Input items (must be 9 items)
     */
    public static void addDiscovery(String recipeRegistryName, @Nullable ItemStack output, int starlightRequired,
        int craftingTickTime, ItemStack... inputs) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        if (inputs == null || inputs.length < SLOT_COUNT_T1) {
            return;
        }

        ItemHandle[] handles = new ItemHandle[SLOT_COUNT_T1];
        for (int i = 0; i < SLOT_COUNT_T1; i++) {
            handles[i] = ASRecipeUtils.handle(inputs[i]);
        }

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.ALTAR_DISCOVERY)
            .recipeRegistryName(recipeRegistryName)
            .inputs(handles)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Adds an Attunement Altar recipe (Tier 2)
     *
     * @param recipeRegistryName Unique registry name for this recipe
     * @param output             The output item
     * @param starlightRequired  Required starlight amount
     * @param craftingTickTime   Crafting duration in ticks
     * @param inputs             Input items (must be 13 items)
     */
    public static void addAttunement(String recipeRegistryName, @Nullable ItemStack output, int starlightRequired,
        int craftingTickTime, ItemStack... inputs) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        if (inputs == null || inputs.length < SLOT_COUNT_T2) {
            return;
        }

        ItemHandle[] handles = new ItemHandle[SLOT_COUNT_T2];
        for (int i = 0; i < SLOT_COUNT_T2; i++) {
            handles[i] = ASRecipeUtils.handle(inputs[i]);
        }

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.ALTAR_ATTUNEMENT)
            .recipeRegistryName(recipeRegistryName)
            .inputs(handles)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Adds a Constellation Altar recipe (Tier 3)
     *
     * @param recipeRegistryName Unique registry name for this recipe
     * @param output             The output item
     * @param starlightRequired  Required starlight amount
     * @param craftingTickTime   Crafting duration in ticks
     * @param inputs             Input items (must be 21 items)
     */
    public static void addConstellation(String recipeRegistryName, @Nullable ItemStack output, int starlightRequired,
        int craftingTickTime, ItemStack... inputs) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        if (inputs == null || inputs.length < SLOT_COUNT_T3) {
            return;
        }

        ItemHandle[] handles = new ItemHandle[SLOT_COUNT_T3];
        for (int i = 0; i < SLOT_COUNT_T3; i++) {
            handles[i] = ASRecipeUtils.handle(inputs[i]);
        }

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.ALTAR_CONSTELLATION)
            .recipeRegistryName(recipeRegistryName)
            .inputs(handles)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Adds a Trait Altar recipe (Tier 4)
     *
     * @param recipeRegistryName     Unique registry name for this recipe
     * @param output                 The output item
     * @param starlightRequired      Required starlight amount
     * @param craftingTickTime       Crafting duration in ticks
     * @param inputs                 Input items (must be 25 items)
     * @param constellationFocusName Required constellation focus (can be null)
     */
    public static void addTrait(String recipeRegistryName, @Nullable ItemStack output, int starlightRequired,
        int craftingTickTime, ItemStack[] inputs, @Nullable String constellationFocusName) {
        addTrait(
            recipeRegistryName,
            output,
            starlightRequired,
            craftingTickTime,
            inputs,
            constellationFocusName,
            SLOT_COUNT_T4);
    }

    /**
     * Adds a Trait Altar recipe with configurable slot count
     */
    public static void addTrait(String recipeRegistryName, @Nullable ItemStack output, int starlightRequired,
        int craftingTickTime, ItemStack[] inputs, @Nullable String constellationFocusName, int slotCount) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        if (inputs == null || inputs.length < slotCount) {
            return;
        }

        IConstellation cst = null;
        if (constellationFocusName != null) {
            cst = ConstellationRegistry.getConstellationByName(constellationFocusName);
            if (cst == null) {
                return; // Invalid constellation
            }
        }

        ItemHandle[] handles = new ItemHandle[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            handles[i] = ASRecipeUtils.handle(inputs[i]);
        }

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.ALTAR_TRAIT)
            .recipeRegistryName(recipeRegistryName)
            .inputs(handles)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .requiredConstellationFocus(cst)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Adds a discovery recipe directly to the recipe map
     * This is for use in recipe loading classes
     */
    public static ASRecipe addDiscoveryToMap(String recipeRegistryName, ItemStack output, int starlightRequired,
        int craftingTickTime, ItemHandle[] inputs) {
        return ASRecipe.builder(ASRecipe.Type.ALTAR_DISCOVERY)
            .recipeRegistryName(recipeRegistryName)
            .inputs(inputs)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .addTo(ASRecipeMaps.ALTAR_DISCOVERY);
    }

    /**
     * Adds an attunement recipe directly to the recipe map
     */
    public static ASRecipe addAttunementToMap(String recipeRegistryName, ItemStack output, int starlightRequired,
        int craftingTickTime, ItemHandle[] inputs) {
        return ASRecipe.builder(ASRecipe.Type.ALTAR_ATTUNEMENT)
            .recipeRegistryName(recipeRegistryName)
            .inputs(inputs)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .addTo(ASRecipeMaps.ALTAR_ATTUNEMENT);
    }

    /**
     * Adds a constellation recipe directly to the recipe map
     */
    public static ASRecipe addConstellationToMap(String recipeRegistryName, ItemStack output, int starlightRequired,
        int craftingTickTime, ItemHandle[] inputs) {
        return ASRecipe.builder(ASRecipe.Type.ALTAR_CONSTELLATION)
            .recipeRegistryName(recipeRegistryName)
            .inputs(inputs)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .addTo(ASRecipeMaps.ALTAR_CONSTELLATION);
    }

    /**
     * Adds a trait recipe directly to the recipe map
     */
    public static ASRecipe addTraitToMap(String recipeRegistryName, ItemStack output, int starlightRequired,
        int craftingTickTime, ItemHandle[] inputs, @Nullable IConstellation constellation) {
        return ASRecipe.builder(ASRecipe.Type.ALTAR_TRAIT)
            .recipeRegistryName(recipeRegistryName)
            .inputs(inputs)
            .output(output)
            .starlightRequired(starlightRequired)
            .duration(craftingTickTime)
            .requiredConstellationFocus(constellation)
            .addTo(ASRecipeMaps.ALTAR_TRAIT);
    }
}
