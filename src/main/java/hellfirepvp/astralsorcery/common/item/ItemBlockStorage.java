/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlockStorage - Base class for items that store block states
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Item Block Storage (1.7.10)
 * <p>
 * Base class for items that can store block states in NBT.
 * Used by architect wand and exchange wand.
 * <p>
 * Features:
 * - Store block + metadata combinations
 * - Retrieve stored blocks
 * - Clear stored blocks
 * <p>
 * Simplified for 1.7.10:
 * - Uses Block + metadata instead of IBlockState
 * - NBT structure: "storedStates" -> list of {id: blockId, meta: metadata}
 */
public abstract class ItemBlockStorage extends Item {

    private static final String TAG_STORED_STATES = "storedStates";
    private static final String TAG_BLOCK_ID = "id";
    private static final String TAG_BLOCK_META = "meta";
    private static final String TAG_SELECTED_INDEX = "selectedIndex";

    /**
     * Try to store a block at the given position
     *
     * @param storeIn The item stack to store in
     * @param world   The world
     * @param x       X position
     * @param y       Y position
     * @param z       Z position
     */
    public static void tryStoreBlock(ItemStack storeIn, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        // Don't store blocks with TileEntities
        if (world.getTileEntity(x, y, z) != null) {
            return;
        }

        // Don't store unbreakable blocks (like bedrock)
        if (block.getBlockHardness(world, x, y, z) < 0) {
            return;
        }

        // Don't store air
        if (block.isAir(world, x, y, z)) {
            return;
        }

        // Get or create NBT
        NBTTagCompound nbt = storeIn.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            storeIn.setTagCompound(nbt);
        }

        // Get stored states list
        NBTTagList list;
        if (nbt.hasKey(TAG_STORED_STATES)) {
            list = nbt.getTagList(TAG_STORED_STATES, 10); // 10 = NBT_TAG_COMPOUND
        } else {
            list = new NBTTagList();
        }

        // Create block state tag
        NBTTagCompound stateTag = new NBTTagCompound();
        stateTag.setInteger(TAG_BLOCK_ID, Block.getIdFromBlock(block));
        stateTag.setInteger(TAG_BLOCK_META, meta);

        // Add to list
        list.appendTag(stateTag);
        nbt.setTag(TAG_STORED_STATES, list);

        LogHelper.debug("Stored block: " + block.getUnlocalizedName() + ":" + meta);
    }

    /**
     * Get mapped stored states from the item stack
     * Returns a map of BlockAndMeta -> ItemStack
     *
     * @param referenceContainer The item stack
     * @return Map of stored blocks
     */
    public static Map<BlockAndMeta, ItemStack> getMappedStoredStates(ItemStack referenceContainer) {
        List<BlockAndMeta> blockStates = getStoredStates(referenceContainer);
        Map<BlockAndMeta, ItemStack> map = new LinkedHashMap<>();

        for (BlockAndMeta state : blockStates) {
            ItemStack stack = new ItemStack(state.block, 1, state.meta);
            map.put(state, stack);
        }

        return map;
    }

    /**
     * Get stored blocks from the item stack
     *
     * @param referenceContainer The item stack
     * @return List of stored blocks
     */
    private static List<BlockAndMeta> getStoredStates(ItemStack referenceContainer) {
        List<BlockAndMeta> states = new LinkedList<>();

        if (referenceContainer == null || referenceContainer.getItem() == null) {
            return states;
        }

        if (!(referenceContainer.getItem() instanceof ItemBlockStorage)) {
            return states;
        }

        NBTTagCompound nbt = referenceContainer.getTagCompound();
        if (nbt == null || !nbt.hasKey(TAG_STORED_STATES)) {
            return states;
        }

        NBTTagList stored = nbt.getTagList(TAG_STORED_STATES, 10); // 10 = NBT_TAG_COMPOUND
        for (int i = 0; i < stored.tagCount(); i++) {
            NBTTagCompound tag = stored.getCompoundTagAt(i);
            Block block = Block.getBlockById(tag.getInteger(TAG_BLOCK_ID));
            int meta = tag.getInteger(TAG_BLOCK_META);

            if (block != null) {
                states.add(new BlockAndMeta(block, meta));
            }
        }

        return states;
    }

    /**
     * Clear all stored blocks from the item
     *
     * @param player The player holding the item
     */
    public static void tryClearContainerFor(EntityPlayer player) {
        ItemStack used = player.getCurrentEquippedItem();
        if (used != null && used.getItem() instanceof ItemBlockStorage) {
            NBTTagCompound nbt = used.getTagCompound();
            if (nbt != null) {
                nbt.removeTag(TAG_STORED_STATES);
                LogHelper.debug("Cleared stored blocks from wand");
            }
        }
    }

    /**
     * Get the currently selected block index
     *
     * @param stack The item stack
     * @return The selected index (0-based)
     */
    public static int getSelectedIndex(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_SELECTED_INDEX)) {
            return nbt.getInteger(TAG_SELECTED_INDEX);
        }
        return 0; // Default to first block
    }

    /**
     * Set the selected block index
     *
     * @param stack The item stack
     * @param index The index to select
     */
    public static void setSelectedIndex(ItemStack stack, int index) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(TAG_SELECTED_INDEX, index);
    }

    /**
     * Cycle to the next stored block
     *
     * @param stack The item stack
     * @return The new selected index
     */
    public static int cycleSelectedIndex(ItemStack stack) {
        List<BlockAndMeta> stored = getStoredStates(stack);
        if (stored.isEmpty()) {
            return 0;
        }

        int current = getSelectedIndex(stack);
        int next = (current + 1) % stored.size();
        setSelectedIndex(stack, next);

        LogHelper.debug("Cycled to block " + next + ": " + stored.get(next).block.getUnlocalizedName());
        return next;
    }

    /**
     * Get the currently selected block
     *
     * @param stack The item stack
     * @return The selected BlockAndMeta, or null if none stored
     */
    public static BlockAndMeta getSelectedBlock(ItemStack stack) {
        List<BlockAndMeta> stored = getStoredStates(stack);
        if (stored.isEmpty()) {
            return null;
        }

        int index = getSelectedIndex(stack);
        if (index >= stored.size()) {
            index = 0;
            setSelectedIndex(stack, 0);
        }

        return stored.get(index);
    }

    /**
     * Get a random seed for preview rendering
     * Changes every 2 seconds (40 ticks)
     *
     * @param world The world
     * @return Random with deterministic seed
     */
    protected static Random getPreviewRandomFromWorld(World world) {
        long tempSeed = 0x6834F10A91B03F15L;
        tempSeed *= (world.getTotalWorldTime() / 40) << 8;
        return new Random(tempSeed);
    }

    /**
     * Helper class to represent Block + metadata combination
     */
    public static class BlockAndMeta {

        public final Block block;
        public final int meta;

        public BlockAndMeta(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof BlockAndMeta)) return false;
            BlockAndMeta other = (BlockAndMeta) obj;
            return block == other.block && meta == other.meta;
        }

        @Override
        public int hashCode() {
            return Block.getIdFromBlock(block) * 31 + meta;
        }

        @Override
        public String toString() {
            return block.getUnlocalizedName() + ":" + meta;
        }
    }
}
