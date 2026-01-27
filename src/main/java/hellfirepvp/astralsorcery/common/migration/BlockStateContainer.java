/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * BlockStateContainer class for block state management
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.Block;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.block.state.BlockStateContainer
 * In 1.7.10: Blocks use metadata (damage values) instead
 * This class is kept for minimal compatibility but should not be used
 */
@Deprecated
public class BlockStateContainer {

    private final Block block;
    private final IProperty<?>[] properties;

    public BlockStateContainer(Block block, IProperty<?>... properties) {
        this.block = block;
        this.properties = properties;
    }

    public Block getBlock() {
        return block;
    }

    public Collection<IProperty<?>> getProperties() {
        return Collections.unmodifiableCollection(Arrays.asList(properties));
    }

    // Removed getBaseState() - no longer returns IBlockState
    // In 1.7.10, blocks are directly accessed with metadata
}
