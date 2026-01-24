/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IStringSerializable interface for enum variants
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.util.IStringSerializable
 * In 1.7.10: This interface doesn't exist, but enums can provide name()
 */
public interface IStringSerializable {

    /**
     * Get the name for serialization
     */
    String getName();
}
