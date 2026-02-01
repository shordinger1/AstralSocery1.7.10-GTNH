/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base TileEntity class for all AstralSorcery TileEntities
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * AstralBaseTileEntity - Base class for all AstralSorcery TileEntities
 * <p>
 * Provides common functionality including:
 * - Simplified NBT read/write
 * - Automatic data synchronization
 * - Inventory management helper methods
 * - Lifecycle management
 * - Rendering bounds configuration
 * <p>
 * All AstralSorcery TileEntities should extend this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public abstract class AstralBaseTileEntity extends TileEntity {

    // ========== Core Fields ==========

    /**
     * Custom name for display
     */
    protected String customName;

    /**
     * Whether to sync to client on markDirty
     */
    protected boolean needsSync = false;

    // ========== Constructors ==========

    public AstralBaseTileEntity() {
        super();
    }

    // ========== Lifecycle Management ==========

    @Override
    public void validate() {
        super.validate();
        onCreated();
    }

    @Override
    public void invalidate() {
        onDestroyed();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        onUnloaded();
        super.onChunkUnload();
    }

    // ========== Hook Methods ==========

    /**
     * Called when TE is created
     */
    protected void onCreated() {}

    /**
     * Called when TE is destroyed
     */
    protected void onDestroyed() {}

    /**
     * Called when chunk is unloaded
     */
    protected void onUnloaded() {}

    // ========== Update Control ==========

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            return;
        }

        // Subclasses can override for custom logic
    }

    // ========== NBT Read/Write ==========

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }

        readCustomNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }

        writeCustomNBT(compound);
    }

    /**
     * Read custom NBT data
     * Subclasses override to read additional data
     *
     * @param compound NBT compound
     */
    protected void readCustomNBT(NBTTagCompound compound) {}

    /**
     * Write custom NBT data
     * Subclasses override to write additional data
     *
     * @param compound NBT compound
     */
    protected void writeCustomNBT(NBTTagCompound compound) {}

    // ========== Data Synchronization ==========

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.func_148857_g();
        readFromNBT(nbt);
    }

    /**
     * Mark for sync to client
     */
    public void markForUpdate() {
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (needsSync && worldObj != null && !worldObj.isRemote) {
            markForUpdate();
        }
    }

    /**
     * Set whether sync is needed on markDirty
     *
     * @param needsSync Whether to sync
     */
    public void setNeedsSync(boolean needsSync) {
        this.needsSync = needsSync;
    }

    // ========== Client Events ==========

    @Override
    public boolean receiveClientEvent(int id, int type) {
        return super.receiveClientEvent(id, type);
    }

    /**
     * Send client event
     *
     * @param id   Event ID
     * @param type Event type
     */
    public void sendClientEvent(int id, int type) {
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), id, type);
        }
    }

    // ========== Rendering ==========

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 4096.0D;
    }

    // ========== Custom Name ==========

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    // ========== Helper Methods ==========

    /**
     * Get block type
     *
     * @return Block instance
     */
    public Block getBlockType() {
        if (blockType == null && worldObj != null) {
            blockType = worldObj.getBlock(xCoord, yCoord, zCoord);
        }
        return blockType;
    }

    /**
     * Get block metadata
     *
     * @return Metadata value
     */
    public int getBlockMetadata() {
        if (blockMetadata == -1 && worldObj != null) {
            blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        }
        return blockMetadata;
    }

    /**
     * Check if server side
     *
     * @return true if server
     */
    public boolean isServerSide() {
        return worldObj != null && !worldObj.isRemote;
    }

    /**
     * Check if client side
     *
     * @return true if client
     */
    public boolean isClientSide() {
        return worldObj != null && worldObj.isRemote;
    }

    /**
     * Get world time
     *
     * @return Total world time
     */
    public long getWorldTime() {
        return worldObj != null ? worldObj.getTotalWorldTime() : 0;
    }

    /**
     * Get distance squared to player
     *
     * @param player Player
     * @return Distance squared
     */
    public double getDistanceSquaredToPlayer(EntityPlayer player) {
        double dx = xCoord + 0.5D - player.posX;
        double dy = yCoord + 0.5D - player.posY;
        double dz = zCoord + 0.5D - player.posZ;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Check if usable by player
     *
     * @param player Player
     * @return true if usable
     */
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj != null && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    // ========== Static Helper Methods - Inventory ==========

    /**
     * Write ItemStack array to NBT
     *
     * @param inventory Item array
     * @param compound  NBT compound
     * @param tagName   Tag name
     */
    public static void writeInventoryToNBT(ItemStack[] inventory, NBTTagCompound compound, String tagName) {
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(itemTag);
                itemList.appendTag(itemTag);
            }
        }
        compound.setTag(tagName, itemList);
    }

    /**
     * Read ItemStack array from NBT
     *
     * @param compound NBT compound
     * @param tagName  Tag name
     * @param size     Array size
     * @return Item array
     */
    public static ItemStack[] readInventoryFromNBT(NBTTagCompound compound, String tagName, int size) {
        ItemStack[] inventory = new ItemStack[size];

        if (compound.hasKey(tagName, 9)) {
            NBTTagList itemList = compound.getTagList(tagName, 10);
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                byte slot = itemTag.getByte("Slot");
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
                }
            }
        }

        return inventory;
    }

    // ========== Inner Class - Simple Inventory ==========

    /**
     * Simple inventory helper class
     * For quick IInventory implementation
     */
    public static class SimpleInventory implements IInventory {

        private final ItemStack[] inventory;
        private final String defaultName;
        private String customName;
        private final AstralBaseTileEntity te;

        public SimpleInventory(AstralBaseTileEntity te, int size, String defaultName) {
            this.te = te;
            this.inventory = new ItemStack[size];
            this.defaultName = defaultName;
        }

        @Override
        public int getSizeInventory() {
            return inventory.length;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot >= 0 && slot < inventory.length ? inventory[slot] : null;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            if (slot >= 0 && slot < inventory.length && inventory[slot] != null) {
                ItemStack stack = inventory[slot];
                if (stack.stackSize <= amount) {
                    inventory[slot] = null;
                    onInventoryChanged();
                    return stack;
                } else {
                    ItemStack result = stack.splitStack(amount);
                    if (stack.stackSize == 0) {
                        inventory[slot] = null;
                    }
                    onInventoryChanged();
                    return result;
                }
            }
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            if (slot >= 0 && slot < inventory.length && inventory[slot] != null) {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            return null;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = stack;
                if (stack != null && stack.stackSize > getInventoryStackLimit()) {
                    stack.stackSize = getInventoryStackLimit();
                }
                onInventoryChanged();
            }
        }

        @Override
        public String getInventoryName() {
            return hasCustomInventoryName() ? customName : defaultName;
        }

        @Override
        public boolean hasCustomInventoryName() {
            return customName != null && !customName.isEmpty();
        }

        public void setCustomName(String name) {
            this.customName = name;
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return te != null && te.isUseableByPlayer(player);
        }

        @Override
        public void openInventory() {}

        @Override
        public void closeInventory() {}

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return true;
        }

        protected void onInventoryChanged() {
            if (te != null) {
                te.markDirty();
            }
        }

        public ItemStack[] getInventory() {
            return inventory;
        }
    }
}
