/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;

/**
 * NEI Recipe Handler for Starlight Infuser
 */
public class InfuserRecipeHandler extends ASRecipeHandler {

    @Override
    public String getRecipeName() {
        return "Astral Sorcery Starlight Infuser";
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/infuser.png";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
            new TemplateRecipeHandler.RecipeTransferRect(
                new Rectangle(74, 23, 25, 18),
                "astralsorcery.infuser",
                new Object[0]));
    }

    @Override
    public Class<?> getGuiClass() {
        return null;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("astralsorcery.infuser")) {
            for (AbstractInfusionRecipe recipe : InfusionRecipeRegistry.recipes) {
                arecipes.add(new InfuserCachedRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (AbstractInfusionRecipe recipe : InfusionRecipeRegistry.recipes) {
            ItemStack output = recipe.getOutput();
            if (output != null && output.isItemEqual(result)) {
                arecipes.add(new InfuserCachedRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (AbstractInfusionRecipe recipe : InfusionRecipeRegistry.recipes) {
            for (ItemStack input : recipe.getRecipeInput()) {
                if (input != null && input.isItemEqual(ingredient)) {
                    arecipes.add(new InfuserCachedRecipe(recipe));
                    break;
                }
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.infuser";
    }

    public static class InfuserCachedRecipe extends ASCachedRecipe {

        private final AbstractInfusionRecipe recipe;

        public InfuserCachedRecipe(AbstractInfusionRecipe recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();

            // Position inputs - grid layout for infuser
            List<ItemStack> recipeInputs = new ArrayList<>(recipe.getRecipeInput());
            int[][] positions = { { 35, 22 }, { 55, 22 }, { 75, 22 }, { 35, 42 }, { 55, 42 }, { 75, 42 } };

            for (int i = 0; i < recipeInputs.size() && i < positions.length; i++) {
                ItemStack stack = recipeInputs.get(i);
                if (stack != null) {
                    this.inputs.add(new PositionedStack(stack, positions[i][0], positions[i][1]));
                }
            }

            ItemStack output = recipe.getOutput();
            if (output != null) {
                this.output = new PositionedStack(output, 107, 32);
            }
        }

        @Override
        public List<ItemStack> getInputItems() {
            return new ArrayList<>(recipe.getRecipeInput());
        }

        @Override
        public ItemStack getOutputItem() {
            return recipe.getOutput();
        }

        @Override
        public Rectangle getOutputPosition() {
            return new Rectangle(107, 32, 16, 16);
        }

        @Override
        public List<Rectangle> getInputPositions() {
            List<Rectangle> positions = new ArrayList<>();
            int[][] posArray = { { 35, 22 }, { 55, 22 }, { 75, 22 }, { 35, 42 }, { 55, 42 }, { 75, 42 } };
            for (int[] pos : posArray) {
                positions.add(new Rectangle(pos[0], pos[1], 16, 16));
            }
            return positions;
        }
    }
}
