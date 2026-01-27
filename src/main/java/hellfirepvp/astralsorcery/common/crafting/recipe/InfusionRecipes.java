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
 * Example Infusion Recipes
 * Demonstrates how to add infusion recipes using the GTNH-style system
 *
 * This replaces the CraftTweaker script-based approach:
 *
 * OLD (ZenScript):
 * mods.astralsorcery.StarlightInfusion.addInfusion(<minecraft:iron_ingot>, <minecraft:gold_ingot>, false, 0.5, 100);
 *
 * NEW (Java):
 * ASRecipe.builder(ASRecipe.Type.INFUSION)
 * .input(ASRecipeUtils.handle(new ItemStack(Items.iron_ingot)))
 * .output(new ItemStack(Items.gold_ingot))
 * .consumeMultiple(false)
 * .consumptionChance(0.5F)
 * .duration(100)
 * .addTo(ASRecipeMaps.INFUSION);
 */
public class InfusionRecipes {

    /**
     * Loads all infusion recipes
     */
    public static void load() {
        // Example: Iron to Gold infusion
        ASRecipe.builder(ASRecipe.Type.INFUSION)
            .inputs(ASRecipeUtils.handle(new ItemStack(Items.iron_ingot)))
            .output(new ItemStack(Items.gold_ingot))
            .consumeMultiple(false)
            .consumptionChance(0.5F)
            .duration(100)
            .addTo(ASRecipeMaps.INFUSION);

        // Example: Using ore dictionary
        // ASRecipe.builder(ASRecipe.Type.INFUSION)
        // .inputs(ASRecipeUtils.oreHandle("ingotIron"))
        // .output(new ItemStack(Items.gold_ingot))
        // .consumeMultiple(false)
        // .consumptionChance(0.5F)
        // .duration(100)
        // .addTo(ASRecipeMaps.INFUSION);

        // Example: Using crystal helper
        // ASRecipe.builder(ASRecipe.Type.INFUSION)
        // .inputs(ASRecipeUtils.anyCrystal())
        // .output(new ItemStack(Items.diamond))
        // .consumeMultiple(true)
        // .consumptionChance(0.3F)
        // .duration(200)
        // .addTo(ASRecipeMaps.INFUSION);
    }
}
