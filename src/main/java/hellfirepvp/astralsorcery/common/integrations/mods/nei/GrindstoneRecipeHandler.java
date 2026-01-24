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

import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;

/**
 * NEI Recipe Handler for Grindstone
 */
public class GrindstoneRecipeHandler extends ASRecipeHandler {

    @Override
    public String getRecipeName() {
        return "Astral Sorcery Grindstone";
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/grindstone.png";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
            new TemplateRecipeHandler.RecipeTransferRect(
                new Rectangle(74, 23, 25, 18),
                "astralsorcery.grindstone",
                new Object[0]));
    }

    @Override
    public Class<?> getGuiClass() {
        return null;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("astralsorcery.grindstone")) {
            for (GrindstoneRecipe recipe : GrindstoneRecipeRegistry.getValidRecipes()) {
                arecipes.add(new GrindstoneCachedRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (GrindstoneRecipe recipe : GrindstoneRecipeRegistry.getValidRecipes()) {
            ItemStack output = recipe.output;
            if (output != null && output.isItemEqual(result)) {
                arecipes.add(new GrindstoneCachedRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (GrindstoneRecipe recipe : GrindstoneRecipeRegistry.getValidRecipes()) {
            ItemStack input = recipe.input.asStack();
            if (input != null && input.isItemEqual(ingredient)) {
                arecipes.add(new GrindstoneCachedRecipe(recipe));
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.grindstone";
    }

    public static class GrindstoneCachedRecipe extends ASCachedRecipe {

        private final GrindstoneRecipe recipe;

        public GrindstoneCachedRecipe(GrindstoneRecipe recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();
            ItemStack inputStack = recipe.input.asStack();
            if (inputStack != null) {
                this.inputs.add(new PositionedStack(inputStack, 35, 22));
            }
            ItemStack outputStack = recipe.output;
            if (outputStack != null) {
                this.output = new PositionedStack(outputStack, 107, 22);
            }
        }

        @Override
        public List<ItemStack> getInputItems() {
            List<ItemStack> items = new ArrayList<>();
            ItemStack input = recipe.input.asStack();
            if (input != null) {
                items.add(input);
            }
            return items;
        }

        @Override
        public ItemStack getOutputItem() {
            return recipe.output;
        }

        @Override
        public Rectangle getOutputPosition() {
            return new Rectangle(107, 22, 16, 16);
        }

        @Override
        public List<Rectangle> getInputPositions() {
            List<Rectangle> positions = new ArrayList<>();
            positions.add(new Rectangle(35, 22, 16, 16));
            return positions;
        }
    }
}
