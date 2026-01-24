/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * PropertyBool class for boolean block properties
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Arrays;
import java.util.Collection;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.block.properties.PropertyBool
 * In 1.7.10: Properties don't exist as classes
 */
public class PropertyBool implements IProperty<Boolean> {

    private final String name;

    protected PropertyBool(String name) {
        this.name = name;
    }

    public static PropertyBool create(String name) {
        return new PropertyBool(name);
    }

    @Override
    public Collection<Boolean> getAllowedValues() {
        return Arrays.asList(true, false);
    }

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getName(Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean parseValue(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public String toString() {
        return name;
    }
}
