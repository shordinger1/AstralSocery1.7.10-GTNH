/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRecipeAccessor
 * Created by HellFirePvP
 * Date: 18.06.2017 / 16:17
 */
public abstract class AbstractRecipeAccessor extends AbstractRecipeData {

    public AbstractRecipeAccessor(@Nonnull ItemStack output) {
        super(output);
    }

    @Nullable
    abstract ItemHandle getExpectedStack(int row, int column);

    @Nullable
    abstract ItemHandle getExpectedStack(ShapedRecipeSlot slot);

    // 1.7.10: IRecipe doesn't have getIngredients() method
    // This method cannot be implemented in 1.7.10 due to fundamental recipe system differences
    public static AbstractRecipeAccessor buildAccessorFor(IRecipe nativeRecipe) {
        throw new UnsupportedOperationException("buildAccessorFor(IRecipe) is not supported in 1.7.10. Use AccessibleRecipeAdapter instead.");
    }

}
