/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Network wrapper for packet handling
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.network.packet.PacketEffect;
import hellfirepvp.astralsorcery.common.network.packet.PacketNBT;
import hellfirepvp.astralsorcery.common.network.packet.PacketTileUpdate;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Network wrapper for Astral Sorcery (1.7.10)
 * <p>
 * Handles all network packet communication between client and server using FML's
 * SimpleNetworkWrapper system.
 * <p>
 * <b>1.7.10 SimpleNetworkWrapper API:</b>
 * <ul>
 * <li>Channel creation: {@link cpw.mods.fml.common.network.NetworkRegistry#newSimpleChannel(String)}</li>
 * <li>Packet registration: {@link cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper#registerMessage}</li>
 * <li>Packet interface: {@link cpw.mods.fml.common.network.simpleimpl.IMessage}</li>
 * <li>Handler interface: {@link cpw.mods.fml.common.network.simpleimpl.IMessageHandler}</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * 
 * <pre>
 * // 1. Define a packet
 * public class PacketTileUpdate implements IMessage {
 *     public int x, y, z;
 *     public NBTTagCompound data;
 *
 *     {@literal @}Override
 *     public void fromBytes(ByteBuf buf) {
 *         x = buf.readInt();
 *         y = buf.readInt();
 *         z = buf.readInt();
 *         data = ByteBufUtils.readTag(buf);
 *     }
 *
 *     {@literal @}Override
 *     public void toBytes(ByteBuf buf) {
 *         buf.writeInt(x);
 *         buf.writeInt(y);
 *         buf.writeInt(z);
 *         ByteBufUtils.writeTag(buf, data);
 *     }
 *
 *     // 2. Define the handler (as a static inner class)
 *     public static class Handler implements IMessageHandler&lt;PacketTileUpdate, IMessage&gt; {
 *         {@literal @}Override
 *         public IMessage onMessage(PacketTileUpdate message, MessageContext ctx) {
 *             // Handle the packet on the receiving side
 *             World world = ctx.getServerHandler().playerEntity.worldObj;
 *             TileEntity te = world.getTileEntity(message.x, message.y, message.z);
 *             if (te instanceof MyTileEntity) {
 *                 ((MyTileEntity) te).readFromNBT(message.data);
 *                 world.markBlockForUpdate(message.x, message.y, message.z);
 *             }
 *             return null; // No response packet
 *         }
 *     }
 * }
 *
 * // 3. Register the packet (in NetworkWrapper.init())
 * NetworkWrapper.registerPacket(
 *     PacketTileUpdate.class,
 *     PacketTileUpdate.Handler.class,
 *     Side.CLIENT
 * );
 *
 * // 4. Send the packet
 * NetworkWrapper.sendToAllAround(new PacketTileUpdate(x, y, z, data),
 *     new NetworkRegistry.TargetPoint(dim, x, y, z, range));
 * </pre>
 * <p>
 * <b>Important Notes:</b>
 * <ul>
 * <li>Each packet must have a unique discriminator ID (auto-assigned)</li>
 * <li>Packets are automatically assigned incrementing IDs starting from 0</li>
 * <li>Packets sent to SERVER are processed on the server thread</li>
 * <li>Packets sent to CLIENT are processed on the client thread</li>
 * <li>Use {@link cpw.mods.fml.common.network.ByteBufUtils} for NBT/String/itemStack serialization</li>
 * </ul>
 */
public class NetworkWrapper {

    private static SimpleNetworkWrapper wrapper;
    private static int nextDiscriminator = 0;

    /**
     * Initialize the network system
     */
    public static void init() {
        LogHelper.entry("NetworkWrapper.init");

        // Create network channel
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

        LogHelper.info("Network channel created: " + Constants.MODID);

        // Register packet types
        // TileEntity update packet (server -> client)
        registerPacket(PacketTileUpdate.class, PacketTileUpdate.Handler.class, Side.CLIENT);

        // NBT data packet (bidirectional)
        registerPacket(PacketNBT.class, PacketNBT.Handler.class, Side.CLIENT);
        registerPacket(PacketNBT.class, PacketNBT.Handler.class, Side.SERVER);

        // Effect packet (server -> client)
        registerPacket(PacketEffect.class, PacketEffect.Handler.class, Side.CLIENT);

        LogHelper.info("Registered 3 packet types");
        LogHelper.exit("NetworkWrapper.init");
    }

    /**
     * Register a packet type
     * <p>
     * 1.7.10 SimpleNetworkWrapper API:
     * 
     * <pre>
     * registerMessage(
     *     IMessageHandler&lt;REQ, ? extends IMessage&gt; handler,
     *     Class&lt;REQ&gt; messageClass,
     *     int id,
     *     Side side
     * )
     * </pre>
     *
     * @param packetClass  The packet class (must extend IMessage)
     * @param handlerClass The packet handler class (must implement IMessageHandler)
     * @param side         The side this packet is processed on
     */
    public static <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<REQ> packetClass,
        Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Side side) {
        if (wrapper == null) {
            throw new IllegalStateException("Network wrapper not initialized!");
        }

        int discriminator = nextDiscriminator++;

        wrapper.registerMessage(handlerClass, packetClass, discriminator, side);

        LogHelper.debug(
            "Registered packet: " + packetClass.getSimpleName() + " (ID: " + discriminator + ") on " + side + " side");
    }

    /**
     * Send a packet to the server
     *
     * @param message The packet to send
     */
    public static void sendToServer(IMessage message) {
        if (wrapper == null) {
            LogHelper.error("Cannot send packet: network wrapper not initialized!");
            return;
        }

        wrapper.sendToServer(message);

        if (Constants.IS_DEBUG) {
            LogHelper.debug(
                "Sent packet to server: " + message.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Send a packet to all clients
     *
     * @param message The packet to send
     */
    public static void sendToAll(IMessage message) {
        if (wrapper == null) {
            LogHelper.error("Cannot send packet: network wrapper not initialized!");
            return;
        }

        wrapper.sendToAll(message);

        if (Constants.IS_DEBUG) {
            LogHelper.debug(
                "Sent packet to all clients: " + message.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Send a packet to a specific client
     *
     * @param message The packet to send
     * @param player  The player to send to (as EntityPlayerMP)
     */
    public static void sendTo(IMessage message, net.minecraft.entity.player.EntityPlayerMP player) {
        if (wrapper == null) {
            LogHelper.error("Cannot send packet: network wrapper not initialized!");
            return;
        }

        wrapper.sendTo(message, player);

        if (Constants.IS_DEBUG) {
            LogHelper.debug(
                "Sent packet to player: " + message.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Send a packet to all players around a point
     *
     * @param message The packet to send
     * @param point   The center point
     */
    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        if (wrapper == null) {
            LogHelper.error("Cannot send packet: network wrapper not initialized!");
            return;
        }

        wrapper.sendToAllAround(message, point);

        if (Constants.IS_DEBUG) {
            LogHelper.debug(
                "Sent packet to all around: " + message.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Send a packet to all players in a dimension
     *
     * @param message   The packet to send
     * @param dimension The dimension ID
     */
    public static void sendToDimension(IMessage message, int dimension) {
        if (wrapper == null) {
            LogHelper.error("Cannot send packet: network wrapper not initialized!");
            return;
        }

        wrapper.sendToDimension(message, dimension);

        if (Constants.IS_DEBUG) {
            LogHelper.debug(
                "Sent packet to dimension " + dimension
                    + ": "
                    + message.getClass()
                        .getSimpleName());
        }
    }

    /**
     * Get the network wrapper instance
     *
     * @return The SimpleNetworkWrapper instance
     */
    public static SimpleNetworkWrapper getWrapper() {
        return wrapper;
    }

    /**
     * Check if the network is initialized
     *
     * @return True if the network wrapper is initialized
     */
    public static boolean isInitialized() {
        return wrapper != null;
    }
}
