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
 * Infusion Recipe helpers using ASRecipe system
 * Replaces CraftTweaker-based InfusionRecipe class
 *
 * Usage:
 * InfusionRecipe.add(new ItemStack(Items.iron_ingot), new ItemStack(Items.gold_ingot), false, 0.5F, 100);
 */
public final class InfusionRecipe {

    private InfusionRecipe() {}

    /**
     * Adds an infusion recipe
     *
     * @param input             The input item
     * @param output            The output item
     * @param consumeMultiple   Whether to consume multiple input items
     * @param consumptionChance Chance (0-1) to consume liquid starlight per tick
     * @param craftingTickTime  Crafting duration in ticks
     */
    public static void add(@Nullable ItemStack input, @Nullable ItemStack output, boolean consumeMultiple,
        float consumptionChance, int craftingTickTime) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }

        ItemHandle in = ASRecipeUtils.handle(input);
        if (in == null) {
            return;
        }

        consumptionChance = ASRecipeUtils.clamp(consumptionChance, 0F, 1F);
        craftingTickTime = Math.max(1, craftingTickTime);

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.INFUSION)
            .inputs(in)
            .output(output)
            .consumeMultiple(consumeMultiple)
            .consumptionChance(consumptionChance)
            .duration(craftingTickTime)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Adds an infusion recipe using ore dictionary
     *
     * @param oreDict           The ore dictionary entry
     * @param output            The output item
     * @param consumeMultiple   Whether to consume multiple input items
     * @param consumptionChance Chance (0-1) to consume liquid starlight per tick
     * @param craftingTickTime  Crafting duration in ticks
     */
    public static void addOre(String oreDict, @Nullable ItemStack output, boolean consumeMultiple,
        float consumptionChance, int craftingTickTime) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }

        ItemHandle in = ASRecipeUtils.oreHandle(oreDict);
        consumptionChance = ASRecipeUtils.clamp(consumptionChance, 0F, 1F);
        craftingTickTime = Math.max(1, craftingTickTime);

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.INFUSION)
            .inputs(in)
            .output(output)
            .consumeMultiple(consumeMultiple)
            .consumptionChance(consumptionChance)
            .duration(craftingTickTime)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Removes all infusion recipes producing the specified output
     *
     * @param output The output to match
     */
    public static void remove(@Nullable ItemStack output) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        ASRecipeMaps.INFUSION.removeRecipesByOutput(output);
    }

    /**
     * Adds an infusion recipe directly to the recipe map
     * This is for use in recipe loading classes
     */
    public static ASRecipe addToMap(ItemHandle input, ItemStack output, boolean consumeMultiple,
        float consumptionChance, int craftingTickTime) {
        consumptionChance = ASRecipeUtils.clamp(consumptionChance, 0F, 1F);
        craftingTickTime = Math.max(1, craftingTickTime);

        return ASRecipe.builder(ASRecipe.Type.INFUSION)
            .inputs(input)
            .output(output)
            .consumeMultiple(consumeMultiple)
            .consumptionChance(consumptionChance)
            .duration(craftingTickTime)
            .addTo(ASRecipeMaps.INFUSION);
    }
}
