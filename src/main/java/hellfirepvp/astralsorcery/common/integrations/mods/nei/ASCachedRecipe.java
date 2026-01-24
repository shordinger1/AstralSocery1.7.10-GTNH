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

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

/**
 * Base class for Astral Sorcery NEI recipe handlers
 * Provides common functionality for all AS machine recipes
 */
public abstract class ASCachedRecipe extends TemplateRecipeHandler.CachedRecipe {

    public ArrayList<PositionedStack> inputs;
    public PositionedStack output;

    @Override
    public PositionedStack getResult() {
        return output;
    }

    @Override
    public ArrayList<PositionedStack> getInput() {
        return inputs;
    }

    /**
     * Get the input items for this recipe
     */
    public abstract List<ItemStack> getInputItems();

    /**
     * Get the output item for this recipe
     */
    public abstract ItemStack getOutputItem();

    /**
     * Get the position for the output slot
     */
    public abstract Rectangle getOutputPosition();

    /**
     * Get positions for input slots
     */
    public abstract List<Rectangle> getInputPositions();
}
