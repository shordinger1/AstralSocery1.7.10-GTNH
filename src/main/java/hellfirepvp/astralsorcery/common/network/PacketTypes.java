/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Packet type definitions and registry
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import java.util.HashMap;
import java.util.Map;

import hellfirepvp.astralsorcery.common.network.packet.AbstractPacket;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Packet type definitions and registry
 */
public class PacketTypes {

    private static final Map<Integer, PacketType> PACKET_TYPES = new HashMap<>();
    private static int nextId = 0;

    public static int registerPacket(String name, Class<? extends AbstractPacket> packetClass,
        Class<? extends cpw.mods.fml.common.network.simpleimpl.IMessageHandler> handlerClass,
        cpw.mods.fml.relauncher.Side side) {
        int id = nextId++;
        PacketType type = new PacketType(id, name, packetClass, handlerClass, side);
        PACKET_TYPES.put(id, type);

        LogHelper.debug("Registered packet: " + name + " (ID: " + id + ", Side: " + side + ")");

        return id;
    }

    public static PacketType getPacketType(int id) {
        return PACKET_TYPES.get(id);
    }

    public static Map<Integer, PacketType> getAllPacketTypes() {
        return new HashMap<>(PACKET_TYPES);
    }

    public static int getPacketCount() {
        return PACKET_TYPES.size();
    }

    public static class PacketType {

        private final int id;
        private final String name;
        private final Class<? extends AbstractPacket> packetClass;
        private final Class<? extends cpw.mods.fml.common.network.simpleimpl.IMessageHandler> handlerClass;
        private final cpw.mods.fml.relauncher.Side side;

        public PacketType(int id, String name, Class<? extends AbstractPacket> packetClass,
            Class<? extends cpw.mods.fml.common.network.simpleimpl.IMessageHandler> handlerClass,
            cpw.mods.fml.relauncher.Side side) {
            this.id = id;
            this.name = name;
            this.packetClass = packetClass;
            this.handlerClass = handlerClass;
            this.side = side;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Class<? extends AbstractPacket> getPacketClass() {
            return packetClass;
        }

        public Class<? extends cpw.mods.fml.common.network.simpleimpl.IMessageHandler> getHandlerClass() {
            return handlerClass;
        }

        public cpw.mods.fml.relauncher.Side getSide() {
            return side;
        }

        @Override
        public String toString() {
            return "PacketType{id=" + id + ", name='" + name + "', side=" + side + "}";
        }
    }
}
