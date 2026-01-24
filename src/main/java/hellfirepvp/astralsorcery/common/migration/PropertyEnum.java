/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * PropertyEnum class for enum block properties
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Arrays;
import java.util.Collection;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.block.properties.PropertyEnum
 * In 1.7.10: Properties don't exist as classes
 */
public class PropertyEnum<T extends Enum<T> & IStringSerializable> implements IProperty<T> {

    private final String name;
    private final Class<T> valueClass;
    private final T[] values;

    protected PropertyEnum(String name, Class<T> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
        this.values = valueClass.getEnumConstants();
    }

    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> valueClass) {
        return new PropertyEnum<>(name, valueClass);
    }

    @Override
    public Collection<T> getAllowedValues() {
        return Arrays.asList(values);
    }

    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getName(T value) {
        return value.getName();
    }

    @Override
    public T parseValue(String value) {
        for (T enumValue : values) {
            if (enumValue.getName()
                .equals(value)) {
                return enumValue;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
