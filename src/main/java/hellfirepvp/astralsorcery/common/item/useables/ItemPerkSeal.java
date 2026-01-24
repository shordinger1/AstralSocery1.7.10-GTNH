/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.InvWrapper;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemPerkSeal
 * Created by HellFirePvP
 * Date: 18.09.2018 / 19:38
 */
public class ItemPerkSeal extends Item {

    public ItemPerkSeal() {
        setMaxStackSize(16);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    public static int getPlayerSealCount(EntityPlayer player) {
        return getPlayerSealCount(new InvWrapper(player.inventory));
    }

    public static int getPlayerSealCount(IItemHandler inv) {
        int count = 0;
        for (ItemStack stack : ItemUtils.findItemsInInventory(inv, new ItemStack(ItemsAS.perkSeal), false)) {
            count += stack.getCount();
        }
        return count;
    }

    public static boolean useSeal(EntityPlayer player, boolean simulate) {
        return useSeal(new InvWrapper(player.inventory), simulate);
    }

    public static boolean useSeal(IItemHandlerModifiable inv, boolean simulate) {
        return ItemUtils.consumeFromInventory(inv, new ItemStack(ItemsAS.perkSeal), simulate);
    }

}
