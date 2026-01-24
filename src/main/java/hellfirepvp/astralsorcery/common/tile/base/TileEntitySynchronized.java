/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:17
 */
public abstract class TileEntitySynchronized extends TileEntity {

    protected static final Random rand = new Random();

    /**
     * Get the position of this TileEntity.
     * In 1.7.10, TileEntity has xCoord, yCoord, zCoord fields instead of getPos().
     */
    public BlockPos getPos() {
        return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
    }

    /**
     * Get the world this TileEntity is in.
     * 1.12.2 compatibility method.
     */
    public net.minecraft.world.World getWorld() {
        return this.worldObj;
    }

    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        readSaveNBT(compound);
    }

    // Both Network & Chunk-saving
    public void readCustomNBT(NBTTagCompound compound) {}

    // Only Network-read
    public void readNetNBT(NBTTagCompound compound) {}

    // Only Chunk-read
    public void readSaveNBT(NBTTagCompound compound) {}

    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeSaveNBT(compound);
    }

    // Both Network & Chunk-saving
    public void writeCustomNBT(NBTTagCompound compound) {}

    // Only Network-write
    public void writeNetNBT(NBTTagCompound compound) {}

    // Only Chunk-write
    public void writeSaveNBT(NBTTagCompound compound) {}

    @Override
    public final S35PacketUpdateTileEntity getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        writeCustomNBT(compound);
        writeNetNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 255, compound);
    }

    public Block getBlockState() {
        return this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public final void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        NBTTagCompound compound = packet.func_148857_g();
        if (compound != null) {
            readCustomNBT(compound);
            readNetNBT(compound);
        }
    }

    public void markForUpdate() {
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            markDirty();
        }
    }

}
