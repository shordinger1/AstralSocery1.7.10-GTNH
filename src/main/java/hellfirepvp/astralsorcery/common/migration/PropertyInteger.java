/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Collection;
import java.util.Collections;

/**
 * Migration class for PropertyInteger (introduced in Minecraft 1.8+).
 * Represents an integer property for block states.
 */
public class PropertyInteger extends PropertyHelper<Integer> {

    private final int min;
    private final int max;

    public PropertyInteger(String name, int min, int max) {
        super(name, Integer.class);
        this.min = min;
        this.max = max;
    }

    @Override
    public Collection<Integer> getAllowedValues() {
        return Collections.singleton(min);
    }

    @Override
    public String getName(Integer value) {
        // In 1.7.10, integer values are just their string representation
        return value.toString();
    }

    @Override
    public Integer parseValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return min; // Return default on parse error
        }
    }

    /**
     * Create a PropertyInteger with the given name and range.
     *
     * @param name the property name
     * @param min  the minimum value (inclusive)
     * @param max  the maximum value (inclusive)
     * @return a new PropertyInteger
     */
    public static PropertyInteger create(String name, int min, int max) {
        return new PropertyInteger(name, min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
