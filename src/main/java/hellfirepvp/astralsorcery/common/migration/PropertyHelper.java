/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Migration class for PropertyHelper (introduced in Minecraft 1.8+).
 * Base class for block state properties.
 */
public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {

    private final Class<T> valueClass;
    private final String name;

    protected PropertyHelper(String name, Class<T> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }
}
