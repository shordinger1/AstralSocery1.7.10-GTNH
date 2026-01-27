/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.SlotItemHandler;

import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationFocusSlot
 * Created by HellFirePvP
 * Date: 06.03.2017 / 14:56
 */
public class ConstellationFocusSlot extends SlotItemHandler {

    private final TileAltar ta;

    public ConstellationFocusSlot(IItemHandler itemHandler, TileAltar ta, int xPosition, int yPosition) {
        super(itemHandler, 100, xPosition, yPosition);
        this.ta = ta;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemConstellationFocus
            && ((ItemConstellationFocus) stack.getItem()).getFocusConstellation(stack) != null;
    }

    @Override
    public ItemStack getStack() {
        return ta.getFocusItem();
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        ta.setFocusStack(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return true;
    }

    // 1.12.2 method - not available in 1.7.10
    // Functionality moved to decrStackSize
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        ta.markForUpdate();
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack focus = ta.getFocusItem();
        ta.setFocusStack(null);
        return focus;
    }

    @Override
    public boolean isSlotInInventory(IInventory inv, int slotIn) {
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

}
