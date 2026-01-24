/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IBlockState interface for block state
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.block.state.IBlockState
 * In 1.7.10: Blocks are represented by Block + metadata
 */
public class IBlockState {

    private final Block block;
    private int metadata;
    private Map<IProperty<?>, Comparable<?>> properties;

    public IBlockState(Block block) {
        this.block = block;
        this.metadata = 0;
        this.properties = new HashMap<>();
    }

    public IBlockState(Block block, int metadata) {
        this.block = block;
        this.metadata = metadata;
        this.properties = new HashMap<>();
    }

    public Block getBlock() {
        return block;
    }

    public int getMetadata() {
        return metadata;
    }

    public <T extends Comparable<T>> IBlockState withProperty(IProperty<T> property, T value) {
        IBlockState newState = new IBlockState(this.block, this.metadata);
        newState.properties = new HashMap<>(this.properties);
        newState.properties.put(property, value);
        return newState;
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        Comparable<?> value = properties.get(property);
        if (value != null) {
            return (T) value;
        }
        // Return default value
        return property.getAllowedValues()
            .iterator()
            .next();
    }

    public Map<IProperty<?>, Comparable<?>> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IBlockState)) return false;
        IBlockState other = (IBlockState) obj;
        return block == other.block && metadata == other.metadata;
    }

    @Override
    public int hashCode() {
        return block.hashCode() * 31 + metadata;
    }
}
