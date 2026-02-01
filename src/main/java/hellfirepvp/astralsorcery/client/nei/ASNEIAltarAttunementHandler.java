/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASNEIAltarAttunementHandler - NEI recipe handler for Attunement Altar
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
 * NEI Recipe Handler for Attunement Altar
 */
public class ASNEIAltarAttunementHandler extends TemplateRecipeHandler {

    // Attunement Altar: 3x3 + 4 corner slots (13 slots)
    private static final int[][] ATTUNEMENT_LAYOUT = {
        // Middle 3x3 grid (slots 1-9)
        { 30, 76 }, { 49, 76 }, { 68, 76 }, { 30, 95 }, { 49, 95 }, { 68, 95 }, { 30, 114 }, { 49, 114 }, { 68, 114 },
        // 4 corner slots (slots 10-13)
        { 11, 57 }, { 87, 57 }, { 11, 133 }, { 87, 133 } };

    private static final int[] OUTPUT_POSITION = { 48, 18 };

    public ASNEIAltarAttunementHandler() {}

    @Override
    public String getRecipeName() {
        return "Attunement Altar";
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.altar.attunement";
    }

    @Override
    public String getGuiTexture() {
        return "astralsorcery:textures/gui/nei/recipetemplatealtarattunement";
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
            List<ASAltarRecipe> recipes = AltarRecipeRegistry.getRecipesForLevel(TileAltar.AltarLevel.ATTUNEMENT);
            System.out.println("[ASNEI Attunement] Loading " + recipes.size() + " recipes");
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
            if (recipe.getAltarLevel() == TileAltar.AltarLevel.ATTUNEMENT) {
                arecipes.add(new CachedAltarRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<ASAltarRecipe> recipes = AltarRecipeRegistry.getRecipesForLevel(TileAltar.AltarLevel.ATTUNEMENT);
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
        return new ASNEIAltarAttunementHandler();
    }

    public class CachedAltarRecipe extends CachedRecipe {

        private final ASAltarRecipe recipe;
        private final List<PositionedStack> inputs;
        private final PositionedStack output;

        public CachedAltarRecipe(ASAltarRecipe recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();

            ItemStack[] recipeInputs = recipe.getInputs();
            for (int i = 0; i < recipeInputs.length && i < ATTUNEMENT_LAYOUT.length; i++) {
                if (recipeInputs[i] != null) {
                    int[] pos = ATTUNEMENT_LAYOUT[i];
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
