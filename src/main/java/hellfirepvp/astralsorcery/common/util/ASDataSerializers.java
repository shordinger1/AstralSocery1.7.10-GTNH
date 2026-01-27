/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;
// TODO: Forge fluid system - manual review needed

import net.minecraft.entity.DataWatcher;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.common.migration.DataSerializer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASDataSerializers
 * Created by HellFirePvP
 * Date: 18.07.2017 / 23:46
 */
public class ASDataSerializers {

    public static DataSerializer<Long> LONG = new DataSerializer<Long>() {

        @Override
        public void write(PacketBuffer buf, Long value) {
            buf.writeLong(value);
        }

        @Override
        public Long read(PacketBuffer buf) {
            return buf.readLong();
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            // 1.7.10: Use Long.valueOf() instead of deprecated new Long()
            return new DataWatcher.WatchableObject(6, id, value != null ? value : Long.valueOf(0));
        }

        public Long copyValue(Long value) {
            return value; // Long is immutable, no copy needed
        }
    };

    public static DataSerializer<Vector3> VECTOR = new DataSerializer<Vector3>() {

        @Override
        public void write(PacketBuffer buf, Vector3 value) {
            buf.writeDouble(value.getX());
            buf.writeDouble(value.getY());
            buf.writeDouble(value.getZ());
        }

        @Override
        public Vector3 read(PacketBuffer buf) {
            // 1.7.10: read() doesn't throw IOException in DataSerializer interface
            return new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            // 1.7.10: type 6 is not used in vanilla, but we'll use it for custom data
            return new DataWatcher.WatchableObject(6, id, value != null ? value : new Vector3());
        }

        public Vector3 copyValue(Vector3 value) {
            return value.clone();
        }
    };

    public static DataSerializer<FluidStack> FLUID = new DataSerializer<FluidStack>() {

        @Override
        public void write(PacketBuffer buf, FluidStack value) {
            buf.writeBoolean(value != null);
            if (value != null) {
                ByteBufUtils.writeFluidStack(buf, value);
            }
        }

        @Override
        public FluidStack read(PacketBuffer buf) {
            return buf.readBoolean() ? ByteBufUtils.readFluidStack(buf) : null;
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            // 1.7.10: type 6 is for complex objects like FluidStack
            return new DataWatcher.WatchableObject(6, id, value);
        }

        public FluidStack copyValue(FluidStack value) {
            return value == null ? null : value.copy();
        }
    };

    public static void registerSerializers() {
        // DataSerializer registration not needed in 1.7.10
        // In 1.12.2+ this would register custom data serializers for network syncing
        // CommonProxy.registryPrimer.register(
        // new DataSerializerEntry(ASDataSerializers.FLUID).setRegistryName(AstralSorcery.MODID, "serializer_fluid"));
        // CommonProxy.registryPrimer.register(
        // new DataSerializerEntry(ASDataSerializers.LONG).setRegistryName(AstralSorcery.MODID, "serializer_long"));
        // CommonProxy.registryPrimer.register(
        // new DataSerializerEntry(ASDataSerializers.VECTOR).setRegistryName(AstralSorcery.MODID, "serializer_vec3d"));
    }

}
