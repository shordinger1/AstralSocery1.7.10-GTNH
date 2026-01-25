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

import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;

/**
 * Example Grindstone Recipes
 * Demonstrates how to add grindstone recipes using the GTNH-style system
 *
 * This replaces the CraftTweaker script-based approach:
 *
 * OLD (ZenScript):
 *   mods.astralsorcery.Grindstone.addRecipe(<minecraft:iron_ore>, <minecraft:dust>, 0.1);
 *
 * NEW (Java):
 *   ASRecipe.builder(ASRecipe.Type.GRINDSTONE)
 *       .input(ASRecipeUtils.handle(new ItemStack(Items.iron_ore)))
 *       .output(new ItemStack(Items.dust))
 *       .doubleChance(0.1F)
 *       .duration(12)
 *       .addTo(ASRecipeMaps.GRINDSTONE);
 */
public class GrindstoneRecipes {

    /**
     * Loads all grindstone recipes
     */
    public static void load() {
        // Example: Iron ore to dust
        // ASRecipe.builder(ASRecipe.Type.GRINDSTONE)
        //     .inputs(ASRecipeUtils.handle(new ItemStack(Items.iron_ore)))
        //     .output(new ItemStack(Items.dust))
        //     .doubleChance(0.1F)
        //     .duration(12)
        //     .addTo(ASRecipeMaps.GRINDSTONE);

        // Example: Using ore dictionary with no double output chance
        // ASRecipe.builder(ASRecipe.Type.GRINDSTONE)
        //     .inputs(ASRecipeUtils.oreHandle("oreGold"))
        //     .output(new ItemStack(Items.gold_nugget, 2))
        //     .doubleChance(0.0F)
        //     .duration(12)
        //     .addTo(ASRecipeMaps.GRINDSTONE);
    }
}
