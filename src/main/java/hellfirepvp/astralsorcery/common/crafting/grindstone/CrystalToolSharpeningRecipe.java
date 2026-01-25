/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import javax.annotation.Nonnull;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalSword;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalToolBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalToolSharpeningRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:50
 */
public class CrystalToolSharpeningRecipe extends GrindstoneRecipe {

    public CrystalToolSharpeningRecipe(int chance) {
        // 1.7.10: Parent class requires non-null input/output, but this recipe handles grinding differently
        // Pass placeholder ItemStack with ItemHandle which accepts null
        super(new ItemHandle((ItemStack) null), new ItemStack(net.minecraft.init.Items.apple, 0, 0), chance);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return !(stackIn == null || stackIn.stackSize <= 0)
            && (stackIn.getItem() instanceof ItemCrystalToolBase || stackIn.getItem() instanceof ItemCrystalSword);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        ToolCrystalProperties prop = ItemCrystalToolBase.getToolProperties(stackIn);
        ToolCrystalProperties result = prop.grindCopy(rand);
        if (result == null) {
            return GrindResult.failBreakItem();
        }
        ItemCrystalToolBase.setToolProperties(stackIn, result);
        if (result.getSize() <= 0) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }

}
