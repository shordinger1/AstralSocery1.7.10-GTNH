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

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * NEI Recipe Handler for Light Well
 */
public class LightWellRecipeHandler extends ASRecipeHandler {

    @Override
    public String getRecipeName() {
        return "Astral Sorcery Light Well";
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/lightWell.png";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
            new TemplateRecipeHandler.RecipeTransferRect(
                new Rectangle(74, 23, 25, 18),
                "astralsorcery.lightwell",
                new Object[0]));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return null; // No GUI for Light Well
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("astralsorcery.lightwell")) {
            for (WellLiquefaction.LiquefactionEntry recipe : WellLiquefaction.getRegisteredLiquefactions()) {
                arecipes.add(new LightWellCachedRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (WellLiquefaction.LiquefactionEntry recipe : WellLiquefaction.getRegisteredLiquefactions()) {
            // Check if result matches output fluid (stored as itemstack for container)
            if (recipe.produced != null) {
                arecipes.add(new LightWellCachedRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (WellLiquefaction.LiquefactionEntry recipe : WellLiquefaction.getRegisteredLiquefactions()) {
            if (recipe.catalyst != null && recipe.catalyst.isItemEqual(ingredient)) {
                arecipes.add(new LightWellCachedRecipe(recipe));
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.lightwell";
    }

    public static class LightWellCachedRecipe extends ASCachedRecipe {

        private final WellLiquefaction.LiquefactionEntry recipe;

        public LightWellCachedRecipe(WellLiquefaction.LiquefactionEntry recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();
            this.inputs.add(new PositionedStack(recipe.catalyst, 35, 22));
            this.output = new PositionedStack(new ItemStack(BlocksAS.blockWell), 107, 22);
        }

        @Override
        public List<ItemStack> getInputItems() {
            List<ItemStack> items = new ArrayList<>();
            items.add(recipe.catalyst);
            return items;
        }

        @Override
        public ItemStack getOutputItem() {
            return new ItemStack(BlocksAS.blockWell);
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
