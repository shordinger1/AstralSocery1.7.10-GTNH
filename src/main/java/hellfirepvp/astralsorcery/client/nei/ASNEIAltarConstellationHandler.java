/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASNEIAltarConstellationHandler - NEI recipe handler for Constellation Altar
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import hellfirepvp.astralsorcery.common.crafting.altar.ASAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * NEI Recipe Handler for Constellation Altar
 */
public class ASNEIAltarConstellationHandler extends TemplateRecipeHandler {

    // Constellation Altar: 3x3 + 4 corners + 8 extra (21 slots)
    private static final int[][] CONSTELLATION_LAYOUT = {
        // Middle 3x3 grid (slots 1-9)
        { 30, 76 }, { 49, 76 }, { 68, 76 }, { 30, 95 }, { 49, 95 }, { 68, 95 }, { 30, 114 }, { 49, 114 }, { 68, 114 },
        // 4 corner slots (slots 10-13)
        { 11, 57 }, { 87, 57 }, { 11, 133 }, { 87, 133 },
        // 8 extra slots (slots 14-21)
        { 30, 57 }, { 68, 57 }, { 11, 76 }, { 87, 76 }, { 11, 114 }, { 87, 114 }, { 30, 133 }, { 68, 133 } };

    private static final int[] OUTPUT_POSITION = { 48, 18 };

    public ASNEIAltarConstellationHandler() {}

    @Override
    public String getRecipeName() {
        return "Constellation Altar";
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.altar.constellation_craft";
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/recipetemplatealtarconstellation";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    public java.awt.Rectangle getBackgroundSize() {
        return new java.awt.Rectangle(116, 162);
    }

    @Override
    public void drawBackground(int recipe) {
        codechicken.lib.gui.GuiDraw.changeTexture(getGuiTexture() + ".png");
        codechicken.lib.gui.GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 116, 162);
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(0, 0, 116, 162), getOverlayIdentifier()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            List<ASAltarRecipe> recipes = AltarRecipeRegistry
                .getRecipesForLevel(TileAltar.AltarLevel.CONSTELLATION_CRAFT);
            System.out.println("[ASNEI Constellation] Loading " + recipes.size() + " recipes");
            for (ASAltarRecipe recipe : recipes) {
                arecipes.add(new CachedAltarRecipe(recipe));
            }
        } else if (outputId.equals("item") && results.length > 0 && results[0] instanceof ItemStack) {
            loadCraftingRecipes((ItemStack) results[0]);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        List<ASAltarRecipe> recipes = AltarRecipeRegistry.findRecipesByOutput(result);
        for (ASAltarRecipe recipe : recipes) {
            if (recipe.getAltarLevel() == TileAltar.AltarLevel.CONSTELLATION_CRAFT) {
                arecipes.add(new CachedAltarRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<ASAltarRecipe> recipes = AltarRecipeRegistry.getRecipesForLevel(TileAltar.AltarLevel.CONSTELLATION_CRAFT);
        for (ASAltarRecipe recipe : recipes) {
            ItemStack[] inputs = recipe.getInputs();
            for (ItemStack input : inputs) {
                if (input != null && itemsMatch(ingredient, input)) {
                    arecipes.add(new CachedAltarRecipe(recipe));
                    break;
                }
            }
        }
    }

    private boolean itemsMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        int metaA = a.getItemDamage();
        int metaB = b.getItemDamage();
        return metaA == metaB || metaA == 32767 || metaB == 32767;
    }

    @Override
    public void drawExtras(int recipe) {
        // Background has all visual elements already
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new ASNEIAltarConstellationHandler();
    }

    public class CachedAltarRecipe extends CachedRecipe {

        private final ASAltarRecipe recipe;
        private final List<PositionedStack> inputs;
        private final PositionedStack output;

        public CachedAltarRecipe(ASAltarRecipe recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();

            ItemStack[] recipeInputs = recipe.getInputs();
            for (int i = 0; i < recipeInputs.length && i < CONSTELLATION_LAYOUT.length; i++) {
                if (recipeInputs[i] != null) {
                    int[] pos = CONSTELLATION_LAYOUT[i];
                    inputs.add(new PositionedStack(recipeInputs[i].copy(), pos[0], pos[1]));
                }
            }

            ItemStack outputStack = recipe.getOutput();
            this.output = outputStack != null
                ? new PositionedStack(outputStack.copy(), OUTPUT_POSITION[0], OUTPUT_POSITION[1])
                : null;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, inputs);
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            List<PositionedStack> other = new ArrayList<>();
            if (output != null) {
                other.add(output);
            }
            return other;
        }
    }
}
