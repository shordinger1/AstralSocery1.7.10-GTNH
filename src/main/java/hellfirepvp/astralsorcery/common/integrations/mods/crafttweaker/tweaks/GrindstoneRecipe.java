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

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;

/**
 * Grindstone Recipe helpers using ASRecipe system
 * Replaces CraftTweaker-based GrindstoneRecipe class
 *
 * Usage:
 * GrindstoneRecipe.add(new ItemStack(Items.iron_ore), new ItemStack(Items.dust), 0.1F);
 */
public final class GrindstoneRecipe {

    private GrindstoneRecipe() {}

    /**
     * Adds a grindstone recipe
     *
     * @param input        The input item
     * @param output       The output item
     * @param doubleChance Chance (0-1) for double output
     */
    public static void add(@Nullable ItemStack input, @Nullable ItemStack output, float doubleChance) {
        add(input, output, doubleChance, 12); // default duration
    }

    /**
     * Adds a grindstone recipe with specified duration
     *
     * @param input        The input item
     * @param output       The output item
     * @param doubleChance Chance (0-1) for double output
     * @param duration     Crafting duration in ticks
     */
    public static void add(@Nullable ItemStack input, @Nullable ItemStack output, float doubleChance, int duration) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }

        ItemHandle in = ASRecipeUtils.handle(input);
        if (in == null) {
            return;
        }

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.GRINDSTONE)
            .inputs(in)
            .output(output)
            .doubleChance(doubleChance)
            .duration(duration)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Adds a grindstone recipe using ore dictionary
     *
     * @param oreDict      The ore dictionary entry
     * @param output       The output item
     * @param doubleChance Chance (0-1) for double output
     */
    public static void addOre(String oreDict, @Nullable ItemStack output, float doubleChance) {
        addOre(oreDict, output, doubleChance, 12); // default duration
    }

    /**
     * Adds a grindstone recipe using ore dictionary with specified duration
     *
     * @param oreDict      The ore dictionary entry
     * @param output       The output item
     * @param doubleChance Chance (0-1) for double output
     * @param duration     Crafting duration in ticks
     */
    public static void addOre(String oreDict, @Nullable ItemStack output, float doubleChance, int duration) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }

        ItemHandle in = ASRecipeUtils.oreHandle(oreDict);

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.GRINDSTONE)
            .inputs(in)
            .output(output)
            .doubleChance(doubleChance)
            .duration(duration)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Removes all grindstone recipes producing the specified output
     *
     * @param output The output to match
     */
    public static void remove(@Nullable ItemStack output) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        ASRecipeMaps.GRINDSTONE.removeRecipesByOutput(output);
    }

    /**
     * Adds a grindstone recipe directly to the recipe map
     * This is for use in recipe loading classes
     */
    public static ASRecipe addToMap(ItemHandle input, ItemStack output, float doubleChance, int duration) {
        return ASRecipe.builder(ASRecipe.Type.GRINDSTONE)
            .inputs(input)
            .output(output)
            .doubleChance(doubleChance)
            .duration(duration)
            .addTo(ASRecipeMaps.GRINDSTONE);
    }
}
