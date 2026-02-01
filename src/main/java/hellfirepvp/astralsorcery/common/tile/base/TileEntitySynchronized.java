/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileEntity Synchronized - Base class with custom NBT handling
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * TileEntitySynchronized - TileEntity base class (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Custom NBT read/write hooks for subclasses</li>
 * <li>Separate network vs save NBT handling</li>
 * <li>Automatic update packet management</li>
 * <li>markForUpdate() for syncing to clients</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>SPacketUpdateTileEntity → S35PacketUpdateTileEntity</li>
 * <li>getUpdatePacket() → getDescriptionPacket()</li>
 * <li>onDataPacket(NetworkManager, SPacketUpdateTileEntity) → onDataPacket(NetworkManager,
 * S35PacketUpdateTileEntity)</li>
 * <li>getPos() → xCoord, yCoord, zCoord</li>
 * <li>IBlockState → Block + metadata</li>
 * <li>world.notifyBlockUpdate() → world.markBlockForUpdate()</li>
 * <li>markDirty() calls world.markTileEntityChunkModified()</li>
 * </ul>
 * <p>
 * <b>NBT Methods:</b>
 * <ul>
 * <li>readCustomNBT() - Called by both network and save</li>
 * <li>writeCustomNBT() - Called by both network and save</li>
 * <li>readNetNBT() - Called only by network packet</li>
 * <li>writeNetNBT() - Called only by network packet</li>
 * <li>readSaveNBT() - Called only by chunk save</li>
 * <li>writeSaveNBT() - Called only by chunk save</li>
 * </ul>
 */
public abstract class TileEntitySynchronized extends TileEntity {

    protected static final Random rand = new Random();

    /**
     * Read from NBT (called by both network and save)
     * 1.7.10: readFromNBT() calls the custom hooks
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        readSaveNBT(compound);
    }

    /**
     * Write to NBT (called by both network and save)
     * 1.7.10: writeToNBT() calls the custom hooks
     */
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeSaveNBT(compound);
    }

    // Both Network & Chunk-save
    public void readCustomNBT(NBTTagCompound compound) {}

    // Only Network-read
    public void readNetNBT(NBTTagCompound compound) {}

    // Only Chunk-read
    public void readSaveNBT(NBTTagCompound compound) {}

    // Both Network & Chunk-save
    public void writeCustomNBT(NBTTagCompound compound) {}

    // Only Network-write
    public void writeNetNBT(NBTTagCompound compound) {}

    // Only Chunk-write
    public void writeSaveNBT(NBTTagCompound compound) {}

    /**
     * Get update packet for client sync
     * 1.7.10: getDescriptionPacket() returns S35PacketUpdateTileEntity
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        writeNetNBT(compound);
        // 1.7.10: S35PacketUpdateTileEntity takes x, y, z, actionType, and NBT
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, compound);
    }

    /**
     * Handle update packet from server
     * 1.7.10: onDataPacket() takes NetworkManager and S35PacketUpdateTileEntity
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        // 1.7.10: Read NBT from packet using func_148857_g()
        NBTTagCompound compound = pkt.func_148857_g();
        if (compound != null) {
            this.readFromNBT(compound);
            readNetNBT(compound);
        }
    }

    /**
     * Mark tile entity for update (sync to client)
     * 1.7.10: Use world.markBlockForUpdate()
     */
    public void markForUpdate() {
        // 1.7.10: Mark block for update to trigger sync
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.markDirty();
        }
    }

    /**
     * Mark tile entity as dirty (needs save)
     * 1.7.10: markDirty() calls world.markTileEntityChunkModified()
     */
    @Override
    public void markDirty() {
        if (this.worldObj != null) {
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    /**
     * Get the block at this tile entity's position
     * 1.7.10: Get block from world using coordinates
     */
    public net.minecraft.block.Block getBlock() {
        if (this.worldObj != null) {
            return this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
        }
        return null;
    }

    /**
     * Get the block metadata at this tile entity's position
     * 1.7.10: Get metadata from world using coordinates
     */
    public int getBlockMetadata() {
        if (this.worldObj != null) {
            return this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        }
        return 0;
    }

}
