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

import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * NEI Recipe Handler for Altar (supports all tiers)
 */
public class AltarRecipeHandler extends ASRecipeHandler {

    private final TileAltar.AltarLevel altarLevel;

    public AltarRecipeHandler(TileAltar.AltarLevel level) {
        this.altarLevel = level;
    }

    @Override
    public String getRecipeName() {
        return "Astral Sorcery Altar " + altarLevel.name();
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/altar" + altarLevel.ordinal() + ".png";
    }

    @Override
    public void loadTransferRects() {
        int yOffset = getAltarOffset();
        transferRects.add(
            new TemplateRecipeHandler.RecipeTransferRect(
                new Rectangle(74, 23 + yOffset, 25, 18),
                getOverlayIdentifier(),
                new Object[0]));
    }

    private int getAltarOffset() {
        switch (altarLevel) {
            case DISCOVERY:
                return 0;
            case ATTUNEMENT:
                return 10;
            case CONSTELLATION_CRAFT:
                return 20;
            case TRAIT_CRAFT:
                return 30;
            default:
                return 0;
        }
    }

    @Override
    public Class<?> getGuiClass() {
        return null;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (AbstractAltarRecipe recipe : AltarRecipeRegistry.recipes.get(altarLevel)) {
                arecipes.add(new AltarCachedRecipe(recipe, altarLevel));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (AbstractAltarRecipe recipe : AltarRecipeRegistry.recipes.get(altarLevel)) {
            ItemStack output = recipe.getOutput();
            if (output != null && output.isItemEqual(result)) {
                arecipes.add(new AltarCachedRecipe(recipe, altarLevel));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (AbstractAltarRecipe recipe : AltarRecipeRegistry.recipes.get(altarLevel)) {
            for (ItemStack input : recipe.getRecipeInput()) {
                if (input != null && input.isItemEqual(ingredient)) {
                    arecipes.add(new AltarCachedRecipe(recipe, altarLevel));
                    break;
                }
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        switch (altarLevel) {
            case DISCOVERY:
                return "astralsorcery.altar.discovery";
            case ATTUNEMENT:
                return "astralsorcery.altar.attunement";
            case CONSTELLATION_CRAFT:
                return "astralsorcery.altar.constellation";
            case TRAIT_CRAFT:
                return "astralsorcery.altar.trait";
            default:
                return "astralsorcery.altar";
        }
    }

    public static class AltarCachedRecipe extends ASCachedRecipe {

        private final AbstractAltarRecipe recipe;
        private final TileAltar.AltarLevel level;

        public AltarCachedRecipe(AbstractAltarRecipe recipe, TileAltar.AltarLevel level) {
            this.recipe = recipe;
            this.level = level;
            this.inputs = new ArrayList<>();

            // Position inputs based on altar level
            List<ItemStack> recipeInputs = recipe.getRecipeInput();
            int[][] positions = getPositionsForLevel(level);

            for (int i = 0; i < recipeInputs.size() && i < positions.length; i++) {
                ItemStack stack = recipeInputs.get(i);
                if (stack != null) {
                    this.inputs.add(new PositionedStack(stack, positions[i][0], positions[i][1]));
                }
            }

            ItemStack output = recipe.getOutput();
            if (output != null) {
                this.output = new PositionedStack(output, 107, 22 + getOffsetForLevel(level));
            }
        }

        private int[][] getPositionsForLevel(TileAltar.AltarLevel level) {
            switch (level) {
                case DISCOVERY:
                    return new int[][] { { 35, 22 }, { 55, 22 }, { 75, 22 } };
                case ATTUNEMENT:
                    return new int[][] { { 35, 22 }, { 55, 22 }, { 75, 22 }, { 35, 42 }, { 55, 42 }, { 75, 42 },
                        { 55, 5 } };
                case CONSTELLATION_CRAFT:
                    return new int[][] { { 35, 22 }, { 55, 22 }, { 75, 22 }, { 35, 42 }, { 55, 42 }, { 75, 42 },
                        { 55, 5 }, { 35, 5 }, { 75, 5 } };
                case TRAIT_CRAFT:
                    return new int[][] { { 35, 22 }, { 55, 22 }, { 75, 22 }, { 35, 42 }, { 55, 42 }, { 75, 42 },
                        { 55, 5 }, { 35, 5 }, { 75, 5 }, { 20, 32 }, { 90, 32 } };
                default:
                    return new int[][] { { 35, 22 } };
            }
        }

        private int getOffsetForLevel(TileAltar.AltarLevel level) {
            switch (level) {
                case ATTUNEMENT:
                    return 10;
                case CONSTELLATION_CRAFT:
                    return 20;
                case TRAIT_CRAFT:
                    return 30;
                default:
                    return 0;
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
            return new Rectangle(107, 22 + getOffsetForLevel(level), 16, 16);
        }

        @Override
        public List<Rectangle> getInputPositions() {
            List<Rectangle> positions = new ArrayList<>();
            int[][] posArray = getPositionsForLevel(level);
            for (int[] pos : posArray) {
                positions.add(new Rectangle(pos[0], pos[1], 16, 16));
            }
            return positions;
        }
    }
}
