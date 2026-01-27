/*******************************************************************************
 * Migrated from OreStages mod API (1.12.2 version)
 * Adapted for Minecraft 1.7.10
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration.net.darkhax.orestages.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;

import hellfirepvp.astralsorcery.common.util.data.Tuple;

/**
 * 1.7.10 Migration of OreTiersAPI from OreStages mod
 * Original: https://github.com/Darkhax-Minecraft/OreStages
 *
 * Adapted to use Block + metadata instead of IBlockState
 */
public final class OreTiersAPI {

    /**
     * A set of all relevant blocks (with metadata).
     */
    private static final Set<BlockMeta> RELEVANT_STATES = new HashSet<>();

    /**
     * A map which links blocks (with metadata) to their stage key and replacement block.
     */
    public static final Map<BlockMeta, Tuple<String, BlockMeta>> STATE_MAP = new HashMap<>();

    /**
     * A map of all the replacement block IDs.
     */
    public static final Map<String, String> REPLACEMENT_IDS = new HashMap<>();

    public static final List<BlockMeta> NON_DEFAULTING = new ArrayList<>();

    /**
     * Adds a replacement for a block.
     *
     * @param stage           The stage to add the replacement to.
     * @param original        The original block.
     * @param originalMeta    The original block meta.
     * @param replacement     The replacement block.
     * @param replacementMeta The replacement block meta.
     * @param defAllow        Whether to allow by default.
     */
    public static void addReplacement(@Nonnull String stage, @Nonnull Block original, int originalMeta,
        @Nonnull Block replacement, int replacementMeta, boolean defAllow) {

        BlockMeta originalKey = new BlockMeta(original, originalMeta);
        BlockMeta replacementKey = new BlockMeta(replacement, replacementMeta);

        if (hasReplacement(originalKey)) {
            // Duplicate replacement - will be replaced
        }

        STATE_MAP.put(originalKey, new Tuple<>(stage, replacementKey));

        addRelevantState(originalKey);
        addRelevantState(replacementKey);

        // 1.7.10: Use GameRegistry unique identifiers instead of registry names
        String originalId = net.minecraft.block.Block.blockRegistry.getNameForObject(original);
        String replacementId = net.minecraft.block.Block.blockRegistry.getNameForObject(replacement);
        if (originalId != null && replacementId != null) {
            REPLACEMENT_IDS.put(originalId, replacementId);
        }

        if (defAllow) {
            NON_DEFAULTING.add(originalKey);
        }
    }

    /**
     * Adds a replacement for a block (metadata 0).
     *
     * @param stage       The stage to add the replacement to.
     * @param original    The original block.
     * @param replacement The block to replace it with.
     * @param defAllow    Whether to allow by default.
     */
    public static void addReplacement(@Nonnull String stage, @Nonnull Block original, @Nonnull Block replacement,
        boolean defAllow) {
        addReplacement(stage, original, 0, replacement, 0, defAllow);
    }

    /**
     * Removes a replacement state.
     *
     * @param state The state to remove.
     */
    public static void removeReplacement(BlockMeta state) {
        STATE_MAP.remove(state);
    }

    /**
     * Checks if a block has a replacement for it.
     *
     * @param block The block to check for.
     * @return Whether or not the block has a replacement.
     */
    public static boolean hasReplacement(Block block) {
        for (BlockMeta key : STATE_MAP.keySet()) {
            if (key.block == block) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific block+meta has a replacement.
     *
     * @param block The block to check for.
     * @param meta  The metadata to check for.
     * @return Whether or not the block+meta has a replacement.
     */
    public static boolean hasReplacement(Block block, int meta) {
        return hasReplacement(new BlockMeta(block, meta));
    }

    /**
     * Checks if a BlockMeta has a replacement.
     *
     * @param state The BlockMeta to check for.
     * @return Whether or not the state has a replacement.
     */
    public static boolean hasReplacement(@Nonnull BlockMeta state) {
        return STATE_MAP.containsKey(state);
    }

    /**
     * Gets a set of all blocks to be replaced/wrapped.
     *
     * @return A set of all the blocks to replace/wrap.
     */
    public static Set<BlockMeta> getStatesToReplace() {
        return STATE_MAP.keySet();
    }

    /**
     * Gets a list of all the relevant block+meta.
     *
     * @return A List of all the relevant blocks.
     */
    public static List<BlockMeta> getRelevantStates() {
        return new ArrayList<>(RELEVANT_STATES);
    }

    /**
     * Gets stage info from a block.
     *
     * @param block The block to get stage info for.
     * @return The stage info for the passed block, or null if not found.
     */
    @Nullable
    public static Tuple<String, BlockMeta> getStageInfo(@Nonnull Block block) {
        for (Map.Entry<BlockMeta, Tuple<String, BlockMeta>> entry : STATE_MAP.entrySet()) {
            if (entry.getKey().block == block) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Gets stage info from a block with metadata.
     *
     * @param block The block to get stage info for.
     * @param meta  The metadata to get stage info for.
     * @return The stage info for the passed block+meta, or null if not found.
     */
    @Nullable
    public static Tuple<String, BlockMeta> getStageInfo(@Nonnull Block block, int meta) {
        return getStageInfo(new BlockMeta(block, meta));
    }

    /**
     * Gets stage info from a BlockMeta.
     *
     * @param state The BlockMeta to get stage info for.
     * @return The stage info for the passed state, or null if not found.
     */
    @Nullable
    public static Tuple<String, BlockMeta> getStageInfo(@Nonnull BlockMeta state) {
        return STATE_MAP.get(state);
    }

    /**
     * Used internally to add a relevant block. Just a wrapper to prevent duplicate entries.
     *
     * @param state The block to add.
     */
    private static void addRelevantState(@Nonnull BlockMeta state) {
        RELEVANT_STATES.add(state);
    }

    /**
     * Helper class to represent Block + metadata combination.
     */
    public static class BlockMeta {

        public final Block block;
        public final int meta;

        public BlockMeta(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof BlockMeta)) return false;
            BlockMeta other = (BlockMeta) obj;
            return block == other.block && meta == other.meta;
        }

        @Override
        public int hashCode() {
            int result = block != null ? System.identityHashCode(block) : 0;
            result = 31 * result + meta;
            return result;
        }

        @Override
        public String toString() {
            return "BlockMeta{" + block + ":" + meta + "}";
        }
    }
}
