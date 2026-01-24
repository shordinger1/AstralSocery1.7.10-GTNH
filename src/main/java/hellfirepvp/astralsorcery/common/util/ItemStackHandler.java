/*******************************************************************************
 * Compatibility Class for ItemStackHandler (1.12.2) in 1.7.10
 * Provides a 1.12.2-compatible item handler API using 1.7.10 IInventory
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

/**
 * 1.7.10 compatibility implementation of ItemStackHandler.
 * In 1.7.10, this wraps IInventory instead of using the Capability system.
 */
public class ItemStackHandler implements IItemHandlerModifiable {

    protected ItemStack[] stacks;
    private final int size;

    public ItemStackHandler(int size) {
        this.size = size;
        this.stacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            this.stacks[i] = null;
        }
    }

    public ItemStackHandler() {
        this(1);
    }

    @Override
    public int getSlots() {
        return this.size;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.size) {
            return null;
        }
        return this.stacks[slot];
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }
        if (slot < 0 || slot >= this.size) {
            return stack;
        }

        ItemStack existing = this.stacks[slot];
        int limit = 64; // Default stack limit

        if (existing != null) {
            if (!ItemStack.areItemStacksEqual(existing, stack)) {
                return stack;
            }
            limit = Math.min(limit, existing.getMaxStackSize());
            if (existing.stackSize >= limit) {
                return stack;
            }

            int space = limit - existing.stackSize;
            int toAdd = Math.min(space, stack.stackSize);
            if (!simulate) {
                existing.stackSize += toAdd;
                onContentsChanged(slot);
            }
            ItemStack remaining = stack.copy();
            remaining.stackSize -= toAdd;
            if (remaining.stackSize <= 0) {
                return null;
            }
            return remaining;
        } else {
            if (!simulate) {
                this.stacks[slot] = stack.copy();
                int toSet = Math.min(limit, stack.stackSize);
                this.stacks[slot].stackSize = toSet;
                onContentsChanged(slot);
            }
            ItemStack remaining = stack.copy();
            remaining.stackSize -= Math.min(limit, stack.stackSize);
            if (remaining.stackSize <= 0) {
                return null;
            }
            return remaining;
        }
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return null;
        }
        if (slot < 0 || slot >= this.size) {
            return null;
        }

        ItemStack existing = this.stacks[slot];
        if (existing == null) {
            return null;
        }

        int toExtract = Math.min(amount, existing.stackSize);
        ItemStack result = existing.copy();
        result.stackSize = toExtract;
        if (!simulate) {
            existing.stackSize -= toExtract;
            if (existing.stackSize <= 0) {
                this.stacks[slot] = null;
            }
            onContentsChanged(slot);
        }
        return result;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= this.size) {
            return;
        }
        this.stacks[slot] = stack;
        onContentsChanged(slot);
    }

    protected void onContentsChanged(int slot) {
        // Override in subclasses if needed
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Size", this.size);
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.size; i++) {
            ItemStack stack = this.stacks[i];
            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                nbtTagList.appendTag(tag);
            }
        }
        nbt.setTag("Items", nbtTagList);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt == null) {
            return;
        }
        NBTTagList tagList = nbt.getTagList("Items", 10);
        if (tagList == null) {
            return;
        }
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            int slot = tag.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.size) {
                this.stacks[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
        onLoad();
    }

    protected void onLoad() {
        // Override in subclasses if needed
    }
}
