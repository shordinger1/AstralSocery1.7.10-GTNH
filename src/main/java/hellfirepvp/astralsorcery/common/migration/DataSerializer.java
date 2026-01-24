/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * DataSerializer interface
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.entity.DataWatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/**
 * Compatibility layer for DataSerializer
 * Handles reading/writing values for DataWatcher
 */
public interface DataSerializer<T> {

    DataSerializer<Byte> VARINT = new DataSerializer<Byte>() {

        @Override
        public void write(PacketBuffer buf, Byte value) {
            buf.writeInt(value & 0xFF);
        }

        @Override
        public Byte read(PacketBuffer buf) {
            return (byte) buf.readInt();
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            return new DataWatcher.WatchableObject(0, id, value); // 0 = Byte type in DataWatcher
        }
    };

    DataSerializer<Integer> INT = new DataSerializer<Integer>() {

        @Override
        public void write(PacketBuffer buf, Integer value) {
            buf.writeInt(value);
        }

        @Override
        public Integer read(PacketBuffer buf) {
            return buf.readInt();
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            return new DataWatcher.WatchableObject(3, id, value); // 3 = Integer type in DataWatcher
        }
    };

    DataSerializer<Float> FLOAT = new DataSerializer<Float>() {

        @Override
        public void write(PacketBuffer buf, Float value) {
            buf.writeFloat(value);
        }

        @Override
        public Float read(PacketBuffer buf) {
            return buf.readFloat();
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            return new DataWatcher.WatchableObject(4, id, value); // 4 = Float type in DataWatcher
        }
    };

    DataSerializer<String> STRING = new DataSerializer<String>() {

        @Override
        public void write(PacketBuffer buf, String value) {
            buf.writeInt(value.length());
            for (char c : value.toCharArray()) {
                buf.writeChar(c);
            }
        }

        @Override
        public String read(PacketBuffer buf) {
            int len = buf.readInt();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append(buf.readChar());
            }
            return sb.toString();
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            return new DataWatcher.WatchableObject(5, id, value); // 5 = String type in DataWatcher
        }
    };

    DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<ItemStack>() {

        @Override
        public void write(PacketBuffer buf, ItemStack value) {
            // Write ItemStack to buffer
            if (value == null) {
                buf.writeInt(-1);
            } else {
                buf.writeInt(value.stackSize);
                buf.writeInt(value.getItemDamage());
                // Simplified - full implementation would write NBT
            }
        }

        @Override
        public ItemStack read(PacketBuffer buf) {
            int size = buf.readInt();
            if (size < 0) return null;
            int damage = buf.readInt();
            // Simplified - would need full NBT reading
            return null;
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            return new DataWatcher.WatchableObject(5, id, value); // 5 = ItemStack type
        }
    };

    DataSerializer<Boolean> BOOLEAN = new DataSerializer<Boolean>() {

        @Override
        public void write(PacketBuffer buf, Boolean value) {
            buf.writeBoolean(value);
        }

        @Override
        public Boolean read(PacketBuffer buf) {
            return buf.readBoolean();
        }

        @Override
        public DataWatcher.WatchableObject createWatchableObject(int id, Object value) {
            return new DataWatcher.WatchableObject(0, id, ((Boolean) value ? 1 : 0)); // 0 = Byte type
        }
    };

    void write(PacketBuffer buf, T value);

    T read(PacketBuffer buf);

    /**
     * Create a WatchableObject with the correct type ID
     * Type IDs: 0=Byte, 1=Short, 2=Integer, 3=Float, 4=String, 5=ItemStack, 6=ChunkCoordinates
     */
    DataWatcher.WatchableObject createWatchableObject(int id, Object value);
}
