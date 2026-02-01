/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * PacketNBT - Generic NBT data transmission packet
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import io.netty.buffer.ByteBuf;

/**
 * Generic packet for transmitting NBT data
 * <p>
 * This packet can be used to send any NBT data between client and server.
 * It includes a type identifier so the receiver knows how to process the data.
 * <p>
 * <b>Common uses:</b>
 * <ul>
 * <li>Sending configuration data</li>
 * <li>Syncing research progress</li>
 * <li>Transmitting constellation data</li>
 * <li>Sending player-specific data</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * 
 * <pre>
 * // Server side - send constellation data to player
 * NBTTagCompound data = new NBTTagCompound();
 * data.setString("constellation", "aevitas");
 * data.setInteger("progress", 50);
 * NetworkWrapper.sendTo(new PacketNBT("constellation_progress", data), player);
 *
 * // Client side - handle in Handler
 * public IMessage onMessage(PacketNBT message, MessageContext ctx) {
 *     String type = message.getType();
 *     NBTTagCompound data = message.getData();
 *     if ("constellation_progress".equals(type)) {
 *         // Process constellation progress
 *     }
 *     return null;
 * }
 * </pre>
 */
public class PacketNBT extends AbstractPacket {

    private String type;
    private NBTTagCompound data;

    /**
     * Default constructor for packet registration
     */
    public PacketNBT() {
        this.type = "";
        this.data = new NBTTagCompound();
    }

    /**
     * Create an NBT packet
     *
     * @param type The type identifier (used to determine how to process the data)
     * @param data The NBT data to send
     */
    public PacketNBT(String type, NBTTagCompound data) {
        this.type = type != null ? type : "";
        this.data = data != null ? data : new NBTTagCompound();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            type = readString(buf, 256);
            data = ByteBufUtils.readTag(buf);

            if (data == null) {
                data = new NBTTagCompound();
                LogHelper.warn("Received null NBT in PacketNBT of type: " + type);
            }
        } catch (Exception e) {
            LogHelper.error("Failed to read PacketNBT", e);
            type = "";
            data = new NBTTagCompound();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            writeString(buf, type);
            ByteBufUtils.writeTag(buf, data);
        } catch (Exception e) {
            LogHelper.error("Failed to write PacketNBT", e);
        }
    }

    /**
     * Packet handler - processes the packet on both client and server
     */
    public static class Handler implements IMessageHandler<PacketNBT, IMessage> {

        @Override
        public IMessage onMessage(PacketNBT message, MessageContext ctx) {
            String type = message.getType();
            NBTTagCompound data = message.getData();

            LogHelper.debug("Received NBT packet: " + type + " on " + ctx.side + " side");

            // Dispatch based on packet type
            switch (type) {
                case "constellation_discovery":
                    handleConstellationDiscovery(data, ctx);
                    break;

                case "research_progress":
                    handleResearchProgress(data, ctx);
                    break;

                case "player_data":
                    handlePlayerData(data, ctx);
                    break;

                case "config_sync":
                    handleConfigSync(data, ctx);
                    break;

                default:
                    LogHelper.warn("Unknown NBT packet type: " + type);
                    break;
            }

            return null; // No response packet
        }

        /**
         * Handle constellation discovery data
         */
        private void handleConstellationDiscovery(NBTTagCompound data, MessageContext ctx) {
            String constellation = data.getString("constellation");
            boolean discovered = data.getBoolean("discovered");

            // TODO: Process constellation discovery
            // Update player's discovered constellations
            LogHelper.debug("Constellation discovery: " + constellation + " -> " + discovered);
        }

        /**
         * Handle research progress data
         */
        private void handleResearchProgress(NBTTagCompound data, MessageContext ctx) {
            // Client-side only: receive full player progress from server
            if (ctx.side.isServer()) {
                LogHelper.warn("Received research_progress packet on server side - ignoring");
                return;
            }

            try {
                hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress =
                    new hellfirepvp.astralsorcery.common.data.research.PlayerProgress();
                progress.load(data);

                // Update client progress
                hellfirepvp.astralsorcery.common.data.research.ResearchManager.receiveProgressFromServer(progress);

                LogHelper.info("Received research progress from server - Tier: " + progress.getTierReached());
            } catch (Exception e) {
                LogHelper.error("Failed to load research progress from packet", e);
            }
        }

        /**
         * Handle player-specific data
         */
        private void handlePlayerData(NBTTagCompound data, MessageContext ctx) {
            // TODO: Process player data
            // This could include perk points, trait selections, etc.
            LogHelper.debug("Received player data packet");
        }

        /**
         * Handle configuration synchronization
         */
        private void handleConfigSync(NBTTagCompound data, MessageContext ctx) {
            // TODO: Process config sync
            // This is used to sync server config to client on connect
            LogHelper.debug("Received config sync packet");
        }
    }

    // Getters
    public String getType() {
        return type;
    }

    public NBTTagCompound getData() {
        return data;
    }

    @Override
    public String getPacketType() {
        return "NBT[" + type + "]";
    }
}
