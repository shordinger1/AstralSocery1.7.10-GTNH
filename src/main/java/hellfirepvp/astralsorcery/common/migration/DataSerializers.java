/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * DataSerializers provides standard serializer instances
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Standard DataSerializer instances for common types
 */
public class DataSerializers {

    public static final DataSerializer<Byte> VARINT = DataSerializer.VARINT;
    public static final DataSerializer<Integer> INT = DataSerializer.INT;
    public static final DataSerializer<Float> FLOAT = DataSerializer.FLOAT;
    public static final DataSerializer<String> STRING = DataSerializer.STRING;
    public static final DataSerializer<Boolean> BOOLEAN = DataSerializer.BOOLEAN;
    public static final DataSerializer<net.minecraft.item.ItemStack> ITEM_STACK = DataSerializer.ITEM_STACK;

    private DataSerializers() {}
}
