/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.item.base.render.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCrystal
 * Created by HellFirePvP
 * Date: 08.05.2016 / 22:08
 */
public class ItemTunedRockCrystal extends ItemTunedCrystalBase implements ItemGatedVisibility {

    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> subItems) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            ItemStack stack;
            for (IWeakConstellation c : ConstellationRegistry.getWeakConstellations()) {
                stack = new ItemStack(this);
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxRockProperties());
                applyMainConstellation(stack, c);
                subItems.add(stack);
            }
        }
    }

    @Override
    public ItemTunedCrystalBase getTunedItemVariant() {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSupposedToSeeInRender(ItemStack stack) {
        return getClientProgress().getTierReached()
            .isThisLaterOrEqual(ProgressionTier.ATTUNEMENT);
    }

}
