/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * PacketEffect - Visual effect synchronization packet
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet;

import net.minecraft.nbt.NBTTagCompound;
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
import hellfirepvp.astralsorcery.common.util.math.Vec3d;
import io.netty.buffer.ByteBuf;

/**
 * Packet for spawning and synchronizing visual effects on clients
 * <p>
 * This packet is sent from server to clients to spawn visual effects
 * like particles, beams, and complex visual sequences.
 * <p>
 * <b>Effect Types:</b>
 * <ul>
 * <li>BEAM - A beam of light between two points</li>
 * <li>BURST - An explosion/burst of particles</li>
 * <li>SPIRAL - A spiral particle effect</li>
 * <li>CONSTELLATION - Constellation discovery effect</li>
 * <li>RITUAL - Ritual activation effect</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * 
 * <pre>
 * // Server side - spawn beam effect
 * Vec3d start = new Vec3d(x1, y1, z1);
 * Vec3d end = new Vec3d(x2, y2, z2);
 * NBTTagCompound data = new NBTTagCompound();
 * data.setInteger("color", 0x00FFFF);
 * NetworkWrapper.sendToAllAround(new PacketEffect("BEAM", start, end, data), new TargetPoint(dimension, x, y, z, 64));
 * </pre>
 */
public class PacketEffect extends AbstractPacket {

    public enum EffectType {
        BEAM,
        BURST,
        SPIRAL,
        CONSTELLATION,
        RITUAL,
        ORBIT,
        TRAIL,
        GENERIC
    }

    private EffectType type;
    private Vec3d position;
    private Vec3d target;
    private NBTTagCompound data;
    private int dimension;
    private int duration;

    /**
     * Default constructor for packet registration
     */
    public PacketEffect() {
        this.type = EffectType.GENERIC;
        this.position = new Vec3d(0, 0, 0);
        this.target = null;
        this.data = new NBTTagCompound();
        this.dimension = 0;
        this.duration = 60; // Default 3 seconds
    }

    /**
     * Create an effect packet at a position
     *
     * @param type     The effect type
     * @param position The position to spawn the effect
     * @param data     Additional effect data (color, size, etc.)
     */
    public PacketEffect(EffectType type, Vec3d position, NBTTagCompound data) {
        this(type, position, null, data, 0, 60);
    }

    /**
     * Create an effect packet with target position
     *
     * @param type      The effect type
     * @param position  The start position
     * @param target    The target position (for beams, etc.)
     * @param data      Additional effect data
     * @param dimension The dimension ID
     * @param duration  Effect duration in ticks
     */
    public PacketEffect(EffectType type, Vec3d position, Vec3d target, NBTTagCompound data, int dimension,
        int duration) {
        this.type = type != null ? type : EffectType.GENERIC;
        this.position = position != null ? position : new Vec3d(0, 0, 0);
        this.target = target;
        this.data = data != null ? data : new NBTTagCompound();
        this.dimension = dimension;
        this.duration = duration;
    }

    /**
     * Create an effect packet at a BlockPos
     *
     * @param type      The effect type
     * @param pos       The position to spawn the effect
     * @param data      Additional effect data
     * @param dimension The dimension ID
     */
    public PacketEffect(EffectType type, BlockPos pos, NBTTagCompound data, int dimension) {
        this(type, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), null, data, dimension, 60);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            // Read effect type
            int typeOrdinal = buf.readInt();
            if (typeOrdinal >= 0 && typeOrdinal < EffectType.values().length) {
                type = EffectType.values()[typeOrdinal];
            } else {
                type = EffectType.GENERIC;
            }

            // Read position
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            position = new Vec3d(x, y, z);

            // Read target (optional)
            boolean hasTarget = buf.readBoolean();
            if (hasTarget) {
                double tx = buf.readDouble();
                double ty = buf.readDouble();
                double tz = buf.readDouble();
                target = new Vec3d(tx, ty, tz);
            } else {
                target = null;
            }

            // Read data
            data = ByteBufUtils.readTag(buf);
            if (data == null) {
                data = new NBTTagCompound();
            }

            // Read dimension and duration
            dimension = buf.readInt();
            duration = buf.readInt();

        } catch (Exception e) {
            LogHelper.error("Failed to read PacketEffect", e);
            type = EffectType.GENERIC;
            position = new Vec3d(0, 0, 0);
            target = null;
            data = new NBTTagCompound();
            dimension = 0;
            duration = 60;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            // Write effect type
            buf.writeInt(type.ordinal());

            // Write position
            buf.writeDouble(position.x);
            buf.writeDouble(position.y);
            buf.writeDouble(position.z);

            // Write target (optional)
            buf.writeBoolean(target != null);
            if (target != null) {
                buf.writeDouble(target.x);
                buf.writeDouble(target.y);
                buf.writeDouble(target.z);
            }

            // Write data
            ByteBufUtils.writeTag(buf, data);

            // Write dimension and duration
            buf.writeInt(dimension);
            buf.writeInt(duration);

        } catch (Exception e) {
            LogHelper.error("Failed to write PacketEffect", e);
        }
    }

    /**
     * Packet handler - processes the packet on the client side
     */
    public static class Handler implements IMessageHandler<PacketEffect, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketEffect message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT) {
                LogHelper.warn("PacketEffect received on server side! Ignoring.");
                return null;
            }

            try {
                // Get the client world
                World world = net.minecraft.client.Minecraft.getMinecraft().theWorld;

                if (world == null) {
                    LogHelper.warn("Client world is null, cannot process effect packet");
                    return null;
                }

                // Check dimension
                if (world.provider.dimensionId != message.dimension) {
                    // Silently ignore effects from other dimensions
                    return null;
                }

                // Spawn the effect
                spawnEffect(message, world);

                if (Constants.IS_DEBUG) {
                    LogHelper.debug("Spawned effect: " + message.type + " at " + message.position);
                }

            } catch (Exception e) {
                LogHelper.error("Failed to process PacketEffect", e);
            }

            return null; // No response packet
        }

        /**
         * Spawn the visual effect
         */
        @SideOnly(Side.CLIENT)
        private void spawnEffect(PacketEffect message, World world) {
            switch (message.type) {
                case BEAM:
                    spawnBeamEffect(message, world);
                    break;

                case BURST:
                    spawnBurstEffect(message, world);
                    break;

                case SPIRAL:
                    spawnSpiralEffect(message, world);
                    break;

                case CONSTELLATION:
                    spawnConstellationEffect(message, world);
                    break;

                case RITUAL:
                    spawnRitualEffect(message, world);
                    break;

                case ORBIT:
                    spawnOrbitEffect(message, world);
                    break;

                case TRAIL:
                    spawnTrailEffect(message, world);
                    break;

                case GENERIC:
                default:
                    spawnGenericEffect(message, world);
                    break;
            }
        }

        @SideOnly(Side.CLIENT)
        private void spawnBeamEffect(PacketEffect message, World world) {
            if (message.target == null) return;

            // TODO: Implement beam effect
            // This would create a beam from position to target
            // Color can be read from message.data.getInteger("color")
            // Width from message.data.getFloat("width")
        }

        @SideOnly(Side.CLIENT)
        private void spawnBurstEffect(PacketEffect message, World world) {
            // TODO: Implement burst effect
            // This would create an explosion-like burst of particles
        }

        @SideOnly(Side.CLIENT)
        private void spawnSpiralEffect(PacketEffect message, World world) {
            // TODO: Implement spiral effect
            // This would create a spiral of particles
        }

        @SideOnly(Side.CLIENT)
        private void spawnConstellationEffect(PacketEffect message, World world) {
            // TODO: Implement constellation discovery effect
            // This would show the constellation being discovered
        }

        @SideOnly(Side.CLIENT)
        private void spawnRitualEffect(PacketEffect message, World world) {
            // TODO: Implement ritual activation effect
            // This would show the ritual activating
        }

        @SideOnly(Side.CLIENT)
        private void spawnOrbitEffect(PacketEffect message, World world) {
            // TODO: Implement orbit effect
            // This would create particles orbiting a point
        }

        @SideOnly(Side.CLIENT)
        private void spawnTrailEffect(PacketEffect message, World world) {
            // TODO: Implement trail effect
            // This would create a trail of particles
        }

        @SideOnly(Side.CLIENT)
        private void spawnGenericEffect(PacketEffect message, World world) {
            // Generic particle effect
            int particleCount = message.data.getInteger("count");
            if (particleCount <= 0) particleCount = 10;
            if (particleCount > 100) particleCount = 100;

            // TODO: Spawn basic particles at position
        }
    }

    // Getters
    public EffectType getType() {
        return type;
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec3d getTarget() {
        return target;
    }

    public NBTTagCompound getData() {
        return data;
    }

    public int getDimension() {
        return dimension;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String getPacketType() {
        return "Effect[" + type + "]";
    }
}
