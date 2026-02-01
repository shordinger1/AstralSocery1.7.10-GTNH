/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * AltarRecipeViewer - Standalone GUI for viewing all altar recipes
 *
 * Phase 4: Independent recipe viewer with search and filtering
 *******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.crafting.altar.ASAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Altar Recipe Viewer
 * <p>
 * Phase 4: Standalone GUI for viewing all altar recipes.
 * Features:
 * - Browse recipes by altar level
 * - Search recipes by output name
 * - View detailed recipe information
 * - Shaped/Shapeless recipe support
 * <p>
 * Open by:
 * - Shift+Right-Click on altar block
 * - Right-Click with Journal
 * - Key binding (if configured)
 */
@cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
public class AltarRecipeViewer extends GuiScreen {

    private TileAltar.AltarLevel currentLevel = TileAltar.AltarLevel.DISCOVERY;
    private List<ASAltarRecipe> currentRecipes = new ArrayList<>();
    private int scrollOffset = 0;
    private GuiTextField searchField;
    private GuiButton[] levelButtons;

    // Layout constants
    private static final int GUI_WIDTH = 300;
    private static final int GUI_HEIGHT = 200;
    private static final int RECIPE_HEIGHT = 80;
    private static final int VISIBLE_RECIPES = 2;

    // Recipe slot layouts for different altar levels
    private static final int[][] DISCOVERY_SLOTS = { { 40, 20 }, { 60, 20 }, { 80, 20 }, { 40, 40 }, { 60, 40 },
        { 80, 40 }, { 40, 60 }, { 60, 60 }, { 80, 60 } };

    private static final int[][] ATTUNEMENT_SLOTS = { { 40, 20 }, { 60, 20 }, { 80, 20 }, { 40, 40 }, { 60, 40 },
        { 80, 40 }, { 40, 60 }, { 60, 60 }, { 80, 60 }, { 40, 80 }, { 60, 80 }, { 80, 80 } };

    public AltarRecipeViewer() {
        // Initialize with Discovery level
        this.currentLevel = TileAltar.AltarLevel.DISCOVERY;
    }

    @Override
    public void initGui() {
        // Calculate center position
        int x = (width - GUI_WIDTH) / 2;
        int y = (height - GUI_HEIGHT) / 2;

        // Initialize search field
        searchField = new GuiTextField(fontRendererObj, x + 80, y + 15, 140, 15);
        searchField.setFocused(false);

        // Initialize level buttons
        levelButtons = new GuiButton[TileAltar.AltarLevel.values().length];
        String[] levelNames = { "Discovery", "Attune", "Const", "Trait", "Brill" };

        for (int i = 0; i < TileAltar.AltarLevel.values().length; i++) {
            final int levelIndex = i;
            final TileAltar.AltarLevel level = TileAltar.AltarLevel.values()[i];

            levelButtons[i] = new GuiButton(i, x + 10 + (i * 58), y + 40, 55, 20, levelNames[i]) {

                @Override
                public void mouseReleased(int x, int y) {
                    // Switch to this level
                    currentLevel = level;
                    scrollOffset = 0;
                    loadRecipes();
                }
            };

            buttonList.add(levelButtons[i]);
        }

        // Load recipes
        loadRecipes();
    }

    private void loadRecipes() {
        currentRecipes = AltarRecipeRegistry.getRecipesForLevel(currentLevel);

        // Apply search filter
        String search = searchField.getText()
            .toLowerCase()
            .trim();
        if (!search.isEmpty()) {
            List<ASAltarRecipe> filtered = new ArrayList<>();
            for (ASAltarRecipe recipe : currentRecipes) {
                ItemStack output = recipe.getOutput();
                if (output != null && output.getDisplayName() != null) {
                    String name = output.getDisplayName()
                        .toLowerCase();
                    if (name.contains(search)) {
                        filtered.add(recipe);
                    }
                }
            }
            currentRecipes = filtered;
        }

        // Adjust scroll offset if needed
        int maxScroll = Math.max(0, currentRecipes.size() - VISIBLE_RECIPES);
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw background
        drawDefaultBackground();

        int x = (width - GUI_WIDTH) / 2;
        int y = (height - GUI_HEIGHT) / 2;

        // Draw title
        String title = EnumChatFormatting.AQUA + "Altar Recipe Viewer";
        drawCenteredString(fontRendererObj, title, width / 2, y + 5, 0xFFFFFF);

        // Draw search label
        fontRendererObj.drawString("Search:", x + 20, y + 20, 0xCCCCCC);

        // Draw level buttons
        for (GuiButton button : levelButtons) {
            button.drawButton(mc, mouseX, mouseY);
        }

        // Draw search field
        searchField.drawTextBox();

        // Draw recipe count
        String count = EnumChatFormatting.GRAY + "Recipes: " + currentRecipes.size();
        fontRendererObj.drawString(count, x + 230, y + 20, 0xCCCCCC);

        // Draw recipes
        drawRecipes(mouseX, mouseY);

        // Draw instructions
        String instructions = EnumChatFormatting.DARK_GRAY + "Scroll: UP/DOWN | Search: Type | Level: Click buttons";
        drawCenteredString(fontRendererObj, instructions, width / 2, y + GUI_HEIGHT + 5, 0x808080);
    }

    private void drawRecipes(int mouseX, int mouseY) {
        int x = (width - GUI_WIDTH) / 2;
        int y = (height - GUI_HEIGHT) / 2;

        int startY = y + 70;

        // Draw visible recipes
        for (int i = 0; i < VISIBLE_RECIPES; i++) {
            int recipeIndex = scrollOffset + i;
            if (recipeIndex >= currentRecipes.size()) break;

            ASAltarRecipe recipe = currentRecipes.get(recipeIndex);
            int recipeY = startY + (i * RECIPE_HEIGHT);

            drawRecipe(recipe, x + 20, recipeY, mouseX, mouseY);
        }
    }

    private void drawRecipe(ASAltarRecipe recipe, int x, int y, int mouseX, int mouseY) {
        // Draw background
        drawGradientRect(x, y, x + 260, y + RECIPE_HEIGHT - 5, 0x40000000, 0x40000000);

        // Draw input items
        ItemStack[] inputs = recipe.getInputs();
        int[][] slots = getSlotsForLevel(currentLevel);

        RenderHelper.enableStandardItemLighting();
        for (int i = 0; i < inputs.length && i < slots.length; i++) {
            if (inputs[i] != null && i < slots.length) {
                int[] slotPos = slots[i];
                int slotX = x + slotPos[0];
                int slotY = y + slotPos[1];

                // Draw item stack
                itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, inputs[i], slotX, slotY);
            }
        }
        RenderHelper.disableStandardItemLighting();

        // Draw arrow
        drawTexturedModalRect(x + 120, y + 30, 0, 0, 20, 20);

        // Draw output item
        ItemStack output = recipe.getOutput();
        if (output != null) {
            RenderHelper.enableStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, output, x + 190, y + 30);
            RenderHelper.disableStandardItemLighting();
        }

        // Draw recipe info
        String type = recipe.isShaped() ? "§9Shaped" : "§7Shapeless";
        fontRendererObj.drawString(type, x + 120, y + 10, 0xCCCCCC);

        String starlight = "§6★" + recipe.getStarlightRequired();
        fontRendererObj.drawString(starlight, x + 120, y + 55, 0xCCCCFF);

        String time = "§e" + recipe.getCraftingTime() + "t";
        fontRendererObj.drawString(time, x + 160, y + 55, 0xCCAA00);

        // Draw constellation if present
        String constellation = recipe.getConstellation();
        if (constellation != null && !constellation.isEmpty()) {
            String constText = "§b" + constellation;
            fontRendererObj.drawString(constText, x + 120, y + 65, 0x55FFFF);
        }

        // Check for hover and show tooltip
        Rectangle rect = new Rectangle(x, y, 260, RECIPE_HEIGHT - 5);
        if (rect.contains(mouseX, mouseY)) {
            // Draw tooltip for output
            if (output != null) {
                List<String> tooltip = output.getTooltip(mc.thePlayer, false);
                drawTooltip(tooltip, mouseX, mouseY);
            }
        }
    }

    private void drawTooltip(List<String> text, int x, int y) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        int width = 0;
        for (String line : text) {
            int w = fontRendererObj.getStringWidth(line);
            if (w > width) width = w;
        }
        int height = text.size() * 10 + 10;
        int x0 = x + 12;
        int y0 = y - 12;

        if (x0 + width > this.width) {
            x0 -= width + 24;
        }

        this.zLevel = 300.0F;
        itemRender.zLevel = 300.0F;

        drawGradientRect(x0 - 3, y0 - 3, x0 + width + 3, y0 + height, 0xC0000000, 0xC0000000);

        for (int i = 0; i < text.size(); i++) {
            fontRendererObj.drawStringWithShadow(text.get(i), x0, y0 + (i * 10), -1);
        }

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private int[][] getSlotsForLevel(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                return DISCOVERY_SLOTS;
            case ATTUNEMENT:
                return ATTUNEMENT_SLOTS;
            // Add more layouts for other levels
            default:
                return DISCOVERY_SLOTS;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < levelButtons.length) {
            // Level button clicked
            currentLevel = TileAltar.AltarLevel.values()[button.id];
            scrollOffset = 0;
            loadRecipes();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (searchField.isFocused()) {
            searchField.textboxKeyTyped(typedChar, keyCode);
            loadRecipes();
        } else {
            // Scroll with keyboard
            if (keyCode == 200) { // Up arrow
                scrollOffset = Math.max(0, scrollOffset - 1);
            } else if (keyCode == 208) { // Down arrow
                int maxScroll = Math.max(0, currentRecipes.size() - VISIBLE_RECIPES);
                scrollOffset = Math.min(maxScroll, scrollOffset + 1);
            } else if (keyCode == 28) { // Enter
                searchField.setFocused(true);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Check if search field was clicked
        int x = (width - GUI_WIDTH) / 2;
        int y = (height - GUI_HEIGHT) / 2;

        if (mouseX >= x + 80 && mouseX <= x + 220 && mouseY >= y + 15 && mouseY <= y + 30) {
            searchField.setFocused(true);
        } else {
            searchField.setFocused(false);
        }

        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        LogHelper.debug("AltarRecipeViewer closed");
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // Don't pause game
    }
}
