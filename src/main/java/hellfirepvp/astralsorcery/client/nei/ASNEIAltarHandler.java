/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASNEIAltarHandler - NEI recipe handler for altar recipes
 *
 * 1.7.10: NEI integration for GT-style recipe system
 *******************************************************************************/

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
 * NEI Recipe Handler for Astral Sorcery Altars
 * <p>
 * Displays altar recipes in NEI for all altar levels.
 * <p>
 * 1.7.10 Implementation:
 * <li>Uses TemplateRecipeHandler for NEI display</li>
 * <li>Supports all 5 altar levels</li>
 * <li>Simple item input → item output display</li>
 */
public class ASNEIAltarHandler extends TemplateRecipeHandler {

    private final TileAltar.AltarLevel altarLevel;
    private final String handlerName;

    // Coordinates from original 1.12.2 JEI integration
    // All backgrounds are 116x162 pixels

    // Discovery Altar: 3x3 grid (9 slots)
    // Layout matches original recipeTemplateAltarDiscovery.png
    private static final int[][] DISCOVERY_LAYOUT = { { 22, 70 }, { 49, 70 }, { 76, 70 }, { 22, 97 }, { 49, 97 },
        { 76, 97 }, { 22, 124 }, { 49, 124 }, { 76, 124 } };

    // Attunement Altar: 3x3 + 4 corner slots (13 slots)
    // Layout matches original recipeTemplateAltarAttunement.png
    private static final int[][] ATTUNEMENT_LAYOUT = {
        // Middle 3x3 grid (slots 1-9)
        { 30, 76 }, { 49, 76 }, { 68, 76 }, { 30, 95 }, { 49, 95 }, { 68, 95 }, { 30, 114 }, { 49, 114 }, { 68, 114 },
        // 4 corner slots (slots 10-13)
        { 11, 57 }, { 87, 57 }, { 11, 133 }, { 87, 133 } };

    // Constellation Altar: 3x3 + 4 corners + 8 extra (21 slots)
    // Layout matches original recipeTemplateAltarConstellation.png
    private static final int[][] CONSTELLATION_LAYOUT = {
        // Middle 3x3 grid (slots 1-9)
        { 30, 76 }, { 49, 76 }, { 68, 76 }, { 30, 95 }, { 49, 95 }, { 68, 95 }, { 30, 114 }, { 49, 114 }, { 68, 114 },
        // 4 corner slots (slots 10-13)
        { 11, 57 }, { 87, 57 }, { 11, 133 }, { 87, 133 },
        // 8 extra slots (slots 14-21)
        { 30, 57 }, { 68, 57 }, { 11, 76 }, { 87, 76 }, { 11, 114 }, { 87, 114 }, { 30, 133 }, { 68, 133 } };

    // Trait Altar: 3x3 + 4 corners + 8 extra + 4 center (25 slots)
    // Layout matches original recipeTemplateAltarTrait.png
    private static final int[][] TRAIT_LAYOUT = {
        // Middle 3x3 grid (slots 1-9)
        { 30, 76 }, { 49, 76 }, { 68, 76 }, { 30, 95 }, { 49, 95 }, { 68, 95 }, { 30, 114 }, { 49, 114 }, { 68, 114 },
        // 4 corner slots (slots 10-13)
        { 11, 57 }, { 87, 57 }, { 11, 133 }, { 87, 133 },
        // 8 extra slots (slots 14-21)
        { 30, 57 }, { 68, 57 }, { 11, 76 }, { 87, 76 }, { 11, 114 }, { 87, 114 }, { 30, 133 }, { 68, 133 },
        // 4 center slots (slots 22-25)
        { 49, 57 }, { 11, 95 }, { 87, 95 }, { 49, 133 } };

    // Brilliance Altar: Same layout as Trait for now
    private static final int[][] BRILLIANCE_LAYOUT = TRAIT_LAYOUT;

    // Output position - same for all altar levels
    private static final int[] OUTPUT_POSITION = { 48, 18 };

    // Default constructor for NEI reflection - should not be used
    public ASNEIAltarHandler() {
        this.altarLevel = TileAltar.AltarLevel.DISCOVERY; // Default fallback
        this.handlerName = "Altar";
    }

    public ASNEIAltarHandler(TileAltar.AltarLevel altarLevel, String handlerName) {
        this.altarLevel = altarLevel;
        this.handlerName = handlerName;
    }

    @Override
    public String getRecipeName() {
        return handlerName != null ? handlerName : "Altar";
    }

    @Override
    public String getOverlayIdentifier() {
        if (altarLevel == null) {
            return "astralsorcery.altar.discovery"; // Fallback
        }
        // Return exact identifier matching catalyst registration
        switch (altarLevel) {
            case DISCOVERY:
                return "astralsorcery.altar.discovery";
            case ATTUNEMENT:
                return "astralsorcery.altar.attunement";
            case CONSTELLATION_CRAFT:
                return "astralsorcery.altar.constellation_craft";
            case TRAIT_CRAFT:
                return "astralsorcery.altar.trait_craft";
            case BRILLIANCE:
                return "astralsorcery.altar.brilliance";
            default:
                return "astralsorcery.altar.discovery";
        }
    }

    @Override
    public String getGuiTexture() {
        // Return custom NEI texture based on altar level
        // NOTE: In 1.7.10, do NOT include .png extension!
        if (altarLevel == null) {
            return "astralsorcery:textures/gui/nei/recipetemplatealtardiscovery";
        }
        switch (altarLevel) {
            case DISCOVERY:
                return "astralsorcery:textures/gui/nei/recipetemplatealtardiscovery";
            case ATTUNEMENT:
                return "astralsorcery:textures/gui/nei/recipetemplatealtarattunement";
            case CONSTELLATION_CRAFT:
                return "astralsorcery:textures/gui/nei/recipetemplatealtarconstellation";
            case TRAIT_CRAFT:
                return "astralsorcery:textures/gui/nei/recipetemplatealtartrait";
            case BRILLIANCE:
                return "astralsorcery:textures/gui/nei/recipetemplatealtartrait"; // Use trait texture for now
            default:
                return "astralsorcery:textures/gui/nei/recipetemplatealtardiscovery";
        }
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    // Set the background size for NEI display area
    public java.awt.Rectangle getBackgroundSize() {
        // Set NEI display area to match our background image size (116x162)
        return new java.awt.Rectangle(116, 162);
    }

    @Override
    public void drawBackground(int recipe) {
        // Manually bind and draw the background texture
        String texture = getGuiTexture();
        if (texture != null) {
            codechicken.lib.gui.GuiDraw.changeTexture(getGuiTexture() + ".png");
            codechicken.lib.gui.GuiDraw.drawTexturedModalRect(0, 0, 0, 0, getBackgroundWidth(), getBackgroundHeight());
        }
    }

    /**
     * Get background size for this altar level
     * All altar levels use 116x162 pixel backgrounds from original
     */
    private int getBackgroundWidth() {
        return 116; // All altar backgrounds are 116px wide
    }

    private int getBackgroundHeight() {
        return 162; // All altar backgrounds are 162px tall
    }

    /**
     * Get input layout for this altar level
     * Phase 4: New helper method
     */
    private int[][] getInputLayout() {
        if (altarLevel == null) {
            return DISCOVERY_LAYOUT; // Default fallback
        }
        switch (altarLevel) {
            case DISCOVERY:
                return DISCOVERY_LAYOUT;
            case ATTUNEMENT:
                return ATTUNEMENT_LAYOUT;
            case CONSTELLATION_CRAFT:
                return CONSTELLATION_LAYOUT;
            case TRAIT_CRAFT:
                return TRAIT_LAYOUT;
            case BRILLIANCE:
                return BRILLIANCE_LAYOUT;
            default:
                return DISCOVERY_LAYOUT;
        }
    }

    @Override
    public void loadTransferRects() {
        // Transfer rect covers the entire 116x162 background
        this.transferRects.add(new RecipeTransferRect(new Rectangle(0, 0, 116, 162), getOverlayIdentifier()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (altarLevel == null) {
            System.out.println("[ASNEI] ERROR: altarLevel is null!");
            return; // Skip if not properly initialized
        }

        System.out.println(
            "[ASNEI] loadCraftingRecipes called - outputId: " + outputId
                + ", expected: "
                + getOverlayIdentifier()
                + ", level: "
                + altarLevel);

        if (outputId.equals(getOverlayIdentifier())) {
            // Load all recipes for this altar level
            java.util.List<ASAltarRecipe> recipes = AltarRecipeRegistry.getRecipesForLevel(altarLevel);
            System.out.println(
                "[ASNEI] Loading " + recipes.size() + " recipes for " + handlerName + " (outputId: " + outputId + ")");
            for (ASAltarRecipe recipe : recipes) {
                arecipes.add(new CachedAltarRecipe(recipe));
            }
        } else if (outputId.equals("item") && results.length > 0 && results[0] instanceof ItemStack) {
            loadCraftingRecipes((ItemStack) results[0]);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (altarLevel == null) {
            return; // Skip if not properly initialized
        }
        // Load recipes that produce this item
        java.util.List<ASAltarRecipe> recipes = AltarRecipeRegistry.findRecipesByOutput(result);
        System.out.println(
            "[ASNEI] Found " + recipes
                .size() + " recipes for output " + result.getDisplayName() + " in " + handlerName);
        for (ASAltarRecipe recipe : recipes) {
            if (recipe.getAltarLevel() == altarLevel) {
                arecipes.add(new CachedAltarRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (altarLevel == null) {
            return; // Skip if not properly initialized
        }
        // Load recipes that use this item
        java.util.List<ASAltarRecipe> recipes = AltarRecipeRegistry.getRecipesForLevel(altarLevel);
        int count = 0;
        for (ASAltarRecipe recipe : recipes) {
            ItemStack[] inputs = recipe.getInputs();
            for (ItemStack input : inputs) {
                if (input != null && itemsMatch(ingredient, input)) {
                    arecipes.add(new CachedAltarRecipe(recipe));
                    count++;
                    break;
                }
            }
        }
        if (count > 0) {
            System.out.println(
                "[ASNEI] Found " + count + " usage recipes for " + ingredient.getDisplayName() + " in " + handlerName);
        }
    }

    /**
     * Check if two ItemStacks match for recipe purposes
     * Supports wildcard metadata (metadata == 32767)
     */
    private boolean itemsMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getItem() != b.getItem()) {
            return false;
        }
        // Check metadata with wildcard support
        int metaA = a.getItemDamage();
        int metaB = b.getItemDamage();
        return metaA == metaB || metaA == 32767 || metaB == 32767;
    }

    @Override
    public void drawExtras(int recipe) {
        if (altarLevel == null) {
            return; // Skip if not properly initialized
        }

        // Do NOT draw text on the background image
        // The original 1.12.2 backgrounds have all the visual elements already
        // We just render the item stacks at their positions
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        // Handle null values for reflection-based instantiation
        TileAltar.AltarLevel level = altarLevel != null ? altarLevel : TileAltar.AltarLevel.DISCOVERY;
        String name = handlerName != null ? handlerName : "Altar";
        return new ASNEIAltarHandler(level, name);
    }

    /**
     * Cached recipe for NEI display
     * Phase 4: Updated to use dynamic layouts
     */
    public class CachedAltarRecipe extends CachedRecipe {

        private final ASAltarRecipe recipe;
        private final List<PositionedStack> inputs;
        private final PositionedStack output;

        public CachedAltarRecipe(ASAltarRecipe recipe) {
            this.recipe = recipe;
            this.inputs = new ArrayList<>();

            // Phase 4: Get layout for altar level
            int[][] layout = getInputLayout();

            // Add input items according to layout
            ItemStack[] recipeInputs = recipe.getInputs();
            for (int i = 0; i < recipeInputs.length && i < layout.length; i++) {
                if (recipeInputs[i] != null) {
                    int[] pos = layout[i];
                    inputs.add(new PositionedStack(recipeInputs[i].copy(), pos[0], pos[1]));
                }
            }

            // Add output item
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

        public ASAltarRecipe getRecipe() {
            return recipe;
        }
    }
}
