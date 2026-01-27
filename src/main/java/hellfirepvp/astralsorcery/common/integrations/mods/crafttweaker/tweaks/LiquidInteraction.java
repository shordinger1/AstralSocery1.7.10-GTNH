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
 * Liquid Interaction Recipe helpers using ASRecipe system
 * Replaces CraftTweaker-based LiquidInteraction class
 *
 * Usage:
 * LiquidInteraction.add(FluidRegistry.WATER, FluidRegistry.LAVA, 1.0F, 1.0F, 10, new ItemStack(Items.diamond));
 */
public final class LiquidInteraction {

    private LiquidInteraction() {}

    /**
     * Adds a liquid interaction recipe
     *
     * @param fluidIn1 First input fluid
     * @param chance1  Consumption chance for fluid 1
     * @param fluidIn2 Second input fluid
     * @param chance2  Consumption chance for fluid 2
     * @param weight   Recipe weight (for selection priority)
     * @param output   Output item
     */
    public static void add(@Nullable Fluid fluidIn1, float chance1, @Nullable Fluid fluidIn2, float chance2, int weight,
        @Nullable ItemStack output) {
        if (output == null || output.stackSize <= 0) {
            return;
        }
        if (fluidIn1 == null || fluidIn2 == null) {
            return;
        }

        FluidStack fs1 = new FluidStack(fluidIn1, 1000);
        FluidStack fs2 = new FluidStack(fluidIn2, 1000);

        weight = Math.max(0, weight);
        chance1 = Math.max(0, chance1);
        chance2 = Math.max(0, chance2);

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.LIQUID_INTERACTION)
            .inputs(ASRecipeUtils.handle(new ItemStack(net.minecraft.init.Items.bucket))) // Placeholder
            .output(output)
            .fluidInput2(fs2)
            .chanceConsumption1(chance1)
            .chanceConsumption2(chance2)
            .weight(weight)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Removes liquid interaction recipes
     *
     * @param fluid1 First fluid (can be null)
     * @param fluid2 Second fluid (can be null)
     * @param output Output item (can be null)
     */
    public static void remove(@Nullable Fluid fluid1, @Nullable Fluid fluid2, @Nullable ItemStack output) {
        // Removal logic depends on how the registry stores these
        // For now, we can remove by output
        if (output != null && output.stackSize > 0) {
            ASRecipeMaps.LIQUID_INTERACTION.removeRecipesByOutput(output);
        }
    }

    /**
     * Adds a liquid interaction recipe directly to the recipe map
     * This is for use in recipe loading classes
     */
    public static ASRecipe addToMap(Fluid fluidIn1, Fluid fluidIn2, float chance1, float chance2, int weight,
        ItemStack output) {
        FluidStack fs1 = new FluidStack(fluidIn1, 1000);
        FluidStack fs2 = new FluidStack(fluidIn2, 1000);

        return ASRecipe.builder(ASRecipe.Type.LIQUID_INTERACTION)
            .inputs(ASRecipeUtils.handle(new ItemStack(net.minecraft.init.Items.bucket)))
            .output(output)
            .fluidInput2(fs2)
            .chanceConsumption1(chance1)
            .chanceConsumption2(chance2)
            .weight(weight)
            .addTo(ASRecipeMaps.LIQUID_INTERACTION);
    }
}
