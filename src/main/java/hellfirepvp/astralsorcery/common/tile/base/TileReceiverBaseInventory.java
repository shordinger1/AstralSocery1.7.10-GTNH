/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileEntity base class with inventory support
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * TileReceiverBaseInventory - TileEntity with inventory support (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Basic inventory management</li>
 * <li>NBT serialization for items</li>
 * <li>Compatibility wrapper for IInventory</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>ItemStackHandler → Custom implementation</li>
 * <li>Capability system → IInventory interface</li>
 * <li>ItemStack.EMPTY → null checks</li>
 * <li>getCount() → stackSize</li>
 * </ul>
 */
public abstract class TileReceiverBaseInventory extends TileEntity implements IInventory {

    protected int inventorySize;
    protected ItemStack[] inventory;

    public TileReceiverBaseInventory(int inventorySize) {
        this.inventorySize = inventorySize;
        this.inventory = new ItemStack[inventorySize];
    }

    /**
     * Get the inventory handler
     * 1.7.10: Returns this instead of ItemHandlerTile
     */
    public TileReceiverBaseInventory getInventoryHandler() {
        return this;
    }

    @Override
    public int getSizeInventory() {
        return inventorySize;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= inventorySize) {
            return null;
        }
        return inventory[slot];
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < 0 || slot >= inventorySize) {
            return null;
        }
        ItemStack stack = inventory[slot];
        if (stack != null) {
            if (stack.stackSize <= amount) {
                inventory[slot] = null;
                markDirty();
                return stack;
            } else {
                ItemStack split = stack.splitStack(amount);
                if (stack.stackSize == 0) {
                    inventory[slot] = null;
                }
                markDirty();
                return split;
            }
        }
        return null;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot < 0 || slot >= inventorySize) {
            return null;
        }
        ItemStack stack = inventory[slot];
        inventory[slot] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= inventorySize) {
            return;
        }
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.getClass()
            .getSimpleName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(@Nonnull net.minecraft.entity.player.EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Read inventory
        for (int i = 0; i < inventorySize; i++) {
            String key = "slot" + i;
            if (compound.hasKey(key)) {
                inventory[i] = ItemStack.loadItemStackFromNBT(compound.getCompoundTag(key));
            } else {
                inventory[i] = null;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        // Write inventory
        for (int i = 0; i < inventorySize; i++) {
            String key = "slot" + i;
            if (inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                inventory[i].writeToNBT(itemTag);
                compound.setTag(key, itemTag);
            } else {
                compound.removeTag(key);
            }
        }
    }

    /**
     * Called when inventory changes
     * Override this in subclasses for custom behavior
     */
    protected void onInventoryChanged(int slotChanged) {
        markDirty();
    }

    /**
     * Simple ItemHandlerTile equivalent
     */
    public static class ItemHandlerTile {

        private final TileReceiverBaseInventory tile;

        public ItemHandlerTile(TileReceiverBaseInventory inv) {
            this.tile = inv;
        }

        public int getSlots() {
            return tile.getSizeInventory();
        }

        @Nonnull
        public ItemStack getStackInSlot(int slot) {
            return tile.getStackInSlot(slot);
        }

        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            tile.setInventorySlotContents(slot, stack);
        }

        public void clearInventory() {
            for (int i = 0; i < getSlots(); i++) {
                setStackInSlot(i, null);
            }
        }

        public int getStackLimit(int slot, ItemStack stack) {
            return tile.getInventoryStackLimit();
        }
    }

    public void updateEntity() {
        // 1.7.10: TileEntity uses updateEntity() for ticking
        // Subclasses should override if needed
    }

    /**
     * Filtered ItemHandlerTile equivalent
     */
    public static class ItemHandlerTileFiltered extends ItemHandlerTile {

        public ItemHandlerTileFiltered(TileReceiverBaseInventory inv) {
            super(inv);
        }

        public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
            return true;
        }

        public boolean canExtractItem(int slot, int amount, @Nonnull ItemStack existing) {
            return true;
        }

        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!canInsertItem(slot, stack, getStackInSlot(slot))) {
                return stack;
            }
            ItemStack existing = getStackInSlot(slot);
            int limit = getStackLimit(slot, stack);
            if (existing == null) {
                ItemStack toInsert = stack.copy();
                if (toInsert.stackSize > limit) {
                    toInsert.stackSize = limit;
                }
                if (!simulate) {
                    setStackInSlot(slot, toInsert);
                }
                return stack.stackSize > toInsert.stackSize
                    ? MiscUtils.copyStackWithSize(stack, stack.stackSize - toInsert.stackSize)
                    : null;
            }
            if (!existing.isItemEqual(stack)) {
                return stack;
            }
            int space = limit - existing.stackSize;
            if (space <= 0) {
                return stack;
            }
            int toAdd = Math.min(space, stack.stackSize);
            if (!simulate) {
                existing.stackSize += toAdd;
            }
            return stack.stackSize > toAdd ? MiscUtils.copyStackWithSize(stack, stack.stackSize - toAdd) : null;
        }

        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!canExtractItem(slot, amount, getStackInSlot(slot))) {
                return null;
            }
            ItemStack existing = getStackInSlot(slot);
            if (existing == null) {
                return null;
            }
            int toExtract = Math.min(amount, existing.stackSize);
            if (!simulate) {
                existing.stackSize -= toExtract;
                if (existing.stackSize <= 0) {
                    setStackInSlot(slot, null);
                }
            }
            return MiscUtils.copyStackWithSize(existing, toExtract);
        }
    }
}
