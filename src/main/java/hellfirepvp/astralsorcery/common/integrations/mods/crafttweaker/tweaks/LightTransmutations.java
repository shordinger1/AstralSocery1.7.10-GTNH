/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * Light Transmutation Recipe helpers using ASRecipe system
 * Replaces CraftTweaker-based LightTransmutations class
 *
 * Usage:
 *   LightTransmutations.add(new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.sand), 1.0);
 */
public final class LightTransmutations {

    private LightTransmutations() {}

    /**
     * Adds a light transmutation recipe
     *
     * @param input  The input item (must be a block)
     * @param output The output item (must be a block)
     * @param cost   Starlight cost
     */
    public static void add(@Nullable ItemStack input, @Nullable ItemStack output, double cost) {
        add(input, output, cost, null);
    }

    /**
     * Adds a light transmutation recipe with constellation requirement
     *
     * @param input               The input item (must be a block)
     * @param output              The output item (must be a block)
     * @param cost                Starlight cost
     * @param constellationName   Required constellation (can be null)
     */
    public static void add(@Nullable ItemStack input, @Nullable ItemStack output, double cost,
        @Nullable String constellationName) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        if (input == null || input.stackSize <= 0) {
            return;
        }

        Block stateIn = ItemUtils.createBlockState(input);
        if (stateIn == null) {
            return;
        }
        Block stateOut = ItemUtils.createBlockState(output);
        if (stateOut == null) {
            return;
        }

        IWeakConstellation req = null;
        if (constellationName != null && !constellationName == null || constellationName.stackSize <= 0) {
            IConstellation cst = ConstellationRegistry.getConstellationByName(constellationName);
            if (cst != null && cst instanceof IWeakConstellation) {
                req = (IWeakConstellation) cst;
            } else {
                return; // Invalid constellation
            }
        }

        cost = Math.max(0, cost);

        ASRecipe recipe = ASRecipe.builder(ASRecipe.Type.LIGHT_TRANSMUTATION)
            .inputs(ASRecipeUtils.handle(input))
            .output(output)
            .transmutationCost(cost)
            .constellation(req)
            .build();

        ModIntegrationCrafttweaker.recipeQueue.add(recipe);
    }

    /**
     * Removes transmutation recipes matching the output
     *
     * @param output    The output to match
     * @param matchMeta Whether to match metadata
     */
    public static void remove(@Nullable ItemStack output, boolean matchMeta) {
        if (!ASRecipeUtils.isOutputValid(output)) {
            return;
        }
        ASRecipeMaps.LIGHT_TRANSMUTATION.removeRecipesByOutput(output);
    }

    /**
     * Adds a transmutation recipe directly to the recipe map
     * This is for use in recipe loading classes
     */
    public static ASRecipe addToMap(ItemStack input, ItemStack output, double cost,
        @Nullable IWeakConstellation constellation) {
        cost = Math.max(0, cost);

        return ASRecipe.builder(ASRecipe.Type.LIGHT_TRANSMUTATION)
            .inputs(ASRecipeUtils.handle(input))
            .output(output)
            .transmutationCost(cost)
            .constellation(constellation)
            .addTo(ASRecipeMaps.LIGHT_TRANSMUTATION);
    }
}
