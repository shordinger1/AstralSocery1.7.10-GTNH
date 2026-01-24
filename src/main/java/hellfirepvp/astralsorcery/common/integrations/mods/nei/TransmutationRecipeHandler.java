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

import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;

/**
 * NEI Recipe Handler for Light Transmutation (Lens crafting)
 */
public class TransmutationRecipeHandler extends ASRecipeHandler {

    @Override
    public String getRecipeName() {
        return "Astral Sorcery Light Transmutation";
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/transmutation.png";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
            new TemplateRecipeHandler.RecipeTransferRect(
                new Rectangle(74, 23, 25, 18),
                "astralsorcery.lightTransmutation",
                new Object[0]));
    }

    @Override
    public Class<?> getGuiClass() {
        return null;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("astralsorcery.lightTransmutation")) {
            for (LightOreTransmutations.Transmutation recipe : LightOreTransmutations.getRegisteredTransmutations()) {
                arecipes.add(new TransmutationCachedRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (LightOreTransmutations.Transmutation recipe : LightOreTransmutations.getRegisteredTransmutations()) {
            ItemStack output = recipe.outputItem;
            if (output != null && output.isItemEqual(result)) {
                arecipes.add(new TransmutationCachedRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (LightOreTransmutations.Transmutation recipe : LightOreTransmutations.getRegisteredTransmutations()) {
            ItemStack input = recipe.inputItem;
            if (input != null && input.isItemEqual(ingredient)) {
                arecipes.add(new TransmutationCachedRecipe(recipe));
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.lightTransmutation";
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 5, 166, 65);
    }

    public static class TransmutationCachedRecipe extends ASCachedRecipe {

        private final LightOreTransmutations.Transmutation recipe;

        public TransmutationCachedRecipe(LightOreTransmutations.Transmutation recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();

            ItemStack input = recipe.inputItem;
            if (input != null) {
                this.inputs.add(new PositionedStack(input, 35, 22));
            }

            ItemStack output = recipe.outputItem;
            if (output != null) {
                this.output = new PositionedStack(output, 107, 22);
            }
        }

        @Override
        public List<ItemStack> getInputItems() {
            List<ItemStack> items = new ArrayList<>();
            items.add(recipe.inputItem);
            return items;
        }

        @Override
        public ItemStack getOutputItem() {
            return recipe.outputItem;
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
