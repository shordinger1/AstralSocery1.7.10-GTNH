/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * PacketTileUpdate - TileEntity synchronization packet
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;
import io.netty.buffer.ByteBuf;

/**
 * Packet for updating TileEntity data on the client
 * <p>
 * This packet sends NBT data from server to client to synchronize
 * TileEntity state changes.
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Server side - send update
 * NBTTagCompound data = new NBTTagCompound();
 * tileEntity.writeToNBT(data);
 * NetworkWrapper.sendToAllAround(new PacketTileUpdate(tileEntity, data), new TargetPoint(dimension, x, y, z, 64));
 * </pre>
 */
public class PacketTileUpdate implements IMessage {

    private int dimension;
    private BlockPos pos;
    private NBTTagCompound data;

    /**
     * Default constructor for packet registration
     */
    public PacketTileUpdate() {
        this.pos = new BlockPos(0, 0, 0);
        this.data = new NBTTagCompound();
    }

    /**
     * Create a tile update packet
     *
     * @param x         X coordinate
     * @param y         Y coordinate
     * @param z         Z coordinate
     * @param dimension Dimension ID
     * @param data      NBT data to send
     */
    public PacketTileUpdate(int x, int y, int z, int dimension, NBTTagCompound data) {
        this.pos = new BlockPos(x, y, z);
        this.dimension = dimension;
        this.data = data;
    }

    /**
     * Create a tile update packet from TileEntity
     * 1.7.10: Note that dimension must be passed separately since
     * TileEntity.worldObj is protected in 1.7.10
     *
     * @param te        The TileEntity to update
     * @param dimension The dimension ID
     * @param data      Custom NBT data (if null, uses full tile data)
     */
    public PacketTileUpdate(TileEntity te, int dimension, NBTTagCompound data) {
        this(te.xCoord, te.yCoord, te.zCoord, dimension, data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            dimension = buf.readInt();
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            data = ByteBufUtils.readTag(buf);

            if (data == null) {
                data = new NBTTagCompound();
                LogHelper.warn("Received null NBT in PacketTileUpdate at " + pos);
            }
        } catch (Exception e) {
            LogHelper.error("Failed to read PacketTileUpdate", e);
            data = new NBTTagCompound();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            buf.writeInt(dimension);
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
            ByteBufUtils.writeTag(buf, data);
        } catch (Exception e) {
            LogHelper.error("Failed to write PacketTileUpdate", e);
        }
    }

    /**
     * Packet handler - processes the packet on the client side
     */
    public static class Handler implements IMessageHandler<PacketTileUpdate, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketTileUpdate message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT) {
                LogHelper.warn("PacketTileUpdate received on server side! Ignoring.");
                return null;
            }

            try {
                // Get the client world
                World world = net.minecraft.client.Minecraft.getMinecraft().theWorld;

                if (world == null) {
                    LogHelper.warn("Client world is null, cannot process TileEntity update");
                    return null;
                }

                // Check dimension
                if (world.provider.dimensionId != message.dimension) {
                    LogHelper.debug(
                        "Ignoring TileEntity update from wrong dimension. " + "Expected: "
                            + world.provider.dimensionId
                            + ", Got: "
                            + message.dimension);
                    return null;
                }

                // Get the TileEntity
                BlockPos pos = message.pos;
                TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

                if (te == null) {
                    LogHelper.debug("TileEntity not found at " + pos + " for update packet");
                    return null;
                }

                // Update the TileEntity
                te.readFromNBT(message.data);
                world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());

                if (Constants.IS_DEBUG) {
                    LogHelper.debug(
                        "Updated TileEntity at " + pos
                            + " of type "
                            + te.getClass()
                                .getSimpleName());
                }

            } catch (Exception e) {
                LogHelper.error("Failed to process PacketTileUpdate", e);
            }

            return null; // No response packet
        }
    }

    // Getters
    public int getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }

    public NBTTagCompound getData() {
        return data;
    }
}
