/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * BlockStateCheck - Interface to check if blocks match criteria
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * BlockStateCheck - Block validation interface (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Check if blocks match specific criteria</li>
 * <li>Support for block type, metadata checks</li>
 * <li>World-specific checks with position awareness</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>IBlockState → Block + metadata</li>
 * <li>BlockPos → int x, y, z</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * // Check for any stone block
 * BlockStateCheck check = new BlockStateCheck.Block(Blocks.stone);
 *
 * // Check for stone with specific metadata
 * BlockStateCheck check = new BlockStateCheck.Meta(Blocks.stone, 0);
 *
 * // Check for stone with multiple metadata values
 * BlockStateCheck check = new BlockStateCheck.AnyMeta(Blocks.stone, 0, 1, 2);
 * </pre>
 */
public interface BlockStateCheck {

    /**
     * Check if block at coordinates is valid
     * 1.7.10: Use block and metadata instead of IBlockState
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if the block matches the criteria
     */
    public boolean isStateValid(World world, int x, int y, int z);

    /**
     * World-specific check interface with additional context
     */
    public static interface WorldSpecific {

        /**
         * Check if block at position is valid
         * 1.7.10: Use int coordinates
         */
        public boolean isStateValid(World world, int x, int y, int z, Block block, int metadata);

        /**
         * Wrap a simple BlockStateCheck as WorldSpecific
         */
        public static WorldSpecific wrap(BlockStateCheck check) {
            return (world, x, y, z, block, metadata) -> check.isStateValid(world, x, y, z);
        }
    }

    /**
     * Check for specific blocks
     */
    public static class BlockCheck implements BlockStateCheck {

        private final List<Block> toCheck;

        public BlockCheck(net.minecraft.block.Block... toCheck) {
            this.toCheck = new ArrayList<>();
            for (Block b : toCheck) {
                this.toCheck.add(b);
            }
        }

        @Override
        public boolean isStateValid(World world, int x, int y, int z) {
            net.minecraft.block.Block block = world.getBlock(x, y, z);
            return toCheck.contains(block);
        }
    }

    /**
     * Check for specific block with exact metadata
     */
    public static class Meta implements BlockStateCheck {

        private final int toCheck;
        private final Block block;

        public Meta(Block block, int toCheck) {
            this.toCheck = toCheck;
            this.block = block;
        }

        public AnyMeta copyWithAdditionalMeta(int add) {
            AnyMeta ret = new AnyMeta(this.block, new ArrayList<>());
            ret.passableMetadataValues.add(toCheck);
            if (!ret.passableMetadataValues.contains(add)) {
                ret.passableMetadataValues.add(add);
            }
            return ret;
        }

        @Override
        public boolean isStateValid(World world, int x, int y, int z) {
            Block b = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            return b.equals(block) && meta == toCheck;
        }
    }

    /**
     * Check for specific block with any of multiple metadata values
     */
    public static class AnyMeta implements BlockStateCheck {

        private final Collection<Integer> passableMetadataValues;
        private final Block block;

        public AnyMeta(Block block, int meta) {
            this(block, new int[] { meta });
        }

        public AnyMeta(Block block, int... values) {
            this.passableMetadataValues = new ArrayList<>(values.length);
            for (int val : values) {
                this.passableMetadataValues.add(val);
            }
            this.block = block;
        }

        public AnyMeta(Block block, Integer... values) {
            this.passableMetadataValues = new ArrayList<>();
            for (Integer val : values) {
                this.passableMetadataValues.add(val);
            }
            this.block = block;
        }

        public AnyMeta(Block block, Collection<Integer> passableMetadataValues) {
            this.passableMetadataValues = passableMetadataValues;
            this.block = block;
        }

        public AnyMeta copyWithAdditionalMeta(int add) {
            AnyMeta ret = new AnyMeta(this.block, this.passableMetadataValues);
            if (!ret.passableMetadataValues.contains(add)) {
                ret.passableMetadataValues.add(add);
            }
            return ret;
        }

        @Override
        public boolean isStateValid(World world, int x, int y, int z) {
            Block b = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            return b.equals(block) && passableMetadataValues.contains(meta);
        }
    }

}
