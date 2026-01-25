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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;

/**
 * Lightwell Recipe helpers using ASRecipe system
 * Replaces CraftTweaker-based WellRecipe class
 *
 * Usage:
 *   WellRecipe.add(new ItemStack(Items.water_bucket), FluidRegistry.WATER, 1.0F, 1.0F, 0xFFFFFF);
 */
public final class WellRecipe {

    private WellRecipe() {}

    /**
     * Adds a lightwell recipe
     *
     * @param input               The input item
     * @param outputFluid         The output fluid
     * @param productionMultiplier Production speed multiplier
     * @param shatterMultiplier    Shatter chance multiplier
     * @param colorHex            Display color (hex)
     */
    public static void add(@Nullable ItemStack input, @Nullable Fluid outputFluid, float productionMultiplier,
        float shatterMultiplier, int colorHex) {
        if (input == null || input.stackSize <= 0) {
            return;
        }
        if (outputFluid == null) {
            return;
        }

        FluidStack fluidOutput = new FluidStack(outputFluid, 1000);

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.WELL)
            .inputs(ASRecipeUtils.handle(input))
            .fluidOutput(fluidOutput)
            .productionMultiplier(productionMultiplier)
            .shatterMultiplier(shatterMultiplier)
            .colorHex(colorHex)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Removes lightwell recipes matching the input
     *
     * @param input       The input item
     * @param outputFluid The output fluid (can be null to match all)
     */
    public static void remove(@Nullable ItemStack input, @Nullable Fluid outputFluid) {
        if (input == null || input.stackSize <= 0) {
            return;
        }

        // Remove from recipe map based on input
        // Implementation depends on how recipes are stored
        ASRecipeMaps.WELL.removeRecipesByOutput(input);
    }

    /**
     * Adds a well recipe directly to the recipe map
     * This is for use in recipe loading classes
     */
    public static ASRecipe addToMap(ItemStack input, Fluid outputFluid, float productionMultiplier,
        float shatterMultiplier, int colorHex) {
        FluidStack fluidOutput = new FluidStack(outputFluid, 1000);

        return ASRecipe.builder(ASRecipe.Type.WELL)
            .inputs(ASRecipeUtils.handle(input))
            .fluidOutput(fluidOutput)
            .productionMultiplier(productionMultiplier)
            .shatterMultiplier(shatterMultiplier)
            .colorHex(colorHex)
            .addTo(ASRecipeMaps.WELL);
    }
}
