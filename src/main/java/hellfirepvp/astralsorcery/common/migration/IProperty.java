/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IProperty interface for block properties
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Collection;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.block.properties.IProperty
 * In 1.7.10: Properties don't exist as classes, blocks use metadata
 */
public interface IProperty<T extends Comparable<T>> {

    /**
     * Get all possible values for this property
     */
    Collection<T> getAllowedValues();

    /**
     * Get the class of the value type
     */
    Class<T> getValueClass();

    /**
     * Get the name of this property
     */
    String getName();

    /**
     * Convert a value to its string representation
     */
    String getName(T value);

    /**
     * Parse a string value
     */
    T parseValue(String value);
}
