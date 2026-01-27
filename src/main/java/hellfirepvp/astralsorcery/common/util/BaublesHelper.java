/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaublesHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 14:14
 */
public class BaublesHelper {

    public static boolean doesPlayerWearBauble(EntityPlayer player, BaubleType inType, ItemStack stack) {
        // 1.7.10: Baubles API returns IInventory, need to iterate slots
        IInventory inv = BaublesApi.getBaubles(player);
        if (inv == null) return false;

        int[] validSlots = getValidSlotsForType(inType);
        for (int slot : validSlots) {
            if (slot < inv.getSizeInventory()) {
                ItemStack worn = inv.getStackInSlot(slot);
                if (worn != null && ItemStack.areItemStacksEqual(worn, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<ItemStack> getWornBaublesForType(EntityPlayer player, BaubleType type) {
        // 1.7.10: BaublesApi.getBaubles() returns IInventory
        IInventory inv = BaublesApi.getBaubles(player);
        List<ItemStack> worn = new ArrayList<>();

        if (inv == null) return worn;

        // 1.7.10: BaubleType doesn't have getValidSlots(), need manual mapping
        int[] validSlots = getValidSlotsForType(type);
        for (int slot : validSlots) {
            if (slot < inv.getSizeInventory()) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (!(stack == null || stack.stackSize <= 0)) {
                    worn.add(stack);
                }
            }
        }
        return worn;
    }

    // 1.7.10: Helper method to map BaubleType to slot numbers
    // In Baubles 1.7.10, only AMULET, RING, and BELT exist
    private static int[] getValidSlotsForType(BaubleType type) {
        if (type == BaubleType.AMULET) {
            return new int[] { 0 };
        } else if (type == BaubleType.RING) {
            return new int[] { 1, 2 };
        } else if (type == BaubleType.BELT) {
            return new int[] { 3 };
        }
        return new int[0];
    }

    public static ItemStack getFirstWornBaublesForType(EntityPlayer player, BaubleType type) {
        List<ItemStack> worn = getWornBaublesForType(player, type);
        return worn.isEmpty() ? null : worn.get(0);
    }

}
