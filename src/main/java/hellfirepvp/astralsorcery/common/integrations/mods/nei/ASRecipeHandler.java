/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.nei;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * Base Recipe Handler for Astral Sorcery machines
 */
public abstract class ASRecipeHandler extends TemplateRecipeHandler {

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 5, 166, 65);
    }

    @Override
    public void drawForeground(int recipe) {
        // Override in subclasses for custom drawing
    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe) {
        // Override in subclasses for custom tooltips
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
        // Override in subclasses for custom item tooltips
        return currenttip;
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyEvent, int recipe) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
        return false;
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    // Abstract methods moved to concrete implementations below
    // public abstract String getRecipeName();
    public abstract String getGuiTexture();
    // public abstract Class<? extends GuiContainer> getGuiClass();
    // public abstract String getOverlayIdentifier();

    @Override
    public String getGuiClass() {
        return null; // Most AS machines don't have GUIs
    }

    @Override
    public String getRecipeName() {
        return AstralSorcery.MODNAME;
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery";
    }
}
