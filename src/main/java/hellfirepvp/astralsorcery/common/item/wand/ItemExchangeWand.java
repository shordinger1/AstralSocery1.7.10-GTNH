/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Exchange Wand - Exchange blocks with stored blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage.BlockAndMeta;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Exchange Wand
 * <p>
 * A wand that exchanges blocks with stored blocks.
 * <p>
 * Features:
 * - Store block types by sneaking + clicking
 * - Exchange blocks in an area (search depth: 5)
 * - Replaces all connected blocks of same type
 * - Display stored blocks in HUD (TODO)
 * <p>
 * Controls:
 * - Sneak + Click block: Store block
 * - Click block: Exchange with stored block
 * - Right-click air: Cycle stored blocks
 * <p>
 * TODO:
 * - Implement HUD rendering (show stored blocks)
 * - Implement block preview rendering
 * - Implement area search (connected blocks)
 * - Implement alignment charge system
 * - Implement particle effects
 * - Implement sound effects
 */
public class ItemExchangeWand extends ItemBlockStorage {

    private static final int SEARCH_DEPTH = 5;

    public ItemExchangeWand() {
        super();
        setMaxStackSize(1); // Only one wand per stack
        setMaxDamage(0); // No durability - infinite use
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Client-side: just return
        if (world.isRemote) {
            return true;
        }

        // Sneaking: Store block
        if (player.isSneaking()) {
            tryStoreBlock(stack, world, x, y, z);

            // Get stored count
            Map<BlockAndMeta, ItemStack> stored = getMappedStoredStates(stack);
            int count = stored.size();

            String color = count > 0 ? "§a" : "§c";
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText(color + "[Exchange Wand] " + "Stored blocks: " + count));

            world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);
            return true;
        }

        // Not sneaking: Exchange blocks
        Map<BlockAndMeta, ItemStack> stored = getMappedStoredStates(stack);
        if (stored.isEmpty()) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    "§c[Exchange Wand] No blocks stored! Sneak + Click to store blocks."));
            return false;
        }

        // Get the currently selected block
        BlockAndMeta toPlace = ItemBlockStorage.getSelectedBlock(stack);
        if (toPlace == null) {
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§c[Exchange Wand] No block selected!"));
            return false;
        }

        // Get target block
        Block targetBlock = world.getBlock(x, y, z);
        int targetMeta = world.getBlockMetadata(x, y, z);

        // Don't exchange unbreakable blocks
        if (targetBlock.getBlockHardness(world, x, y, z) < 0) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§c[Exchange Wand] Cannot exchange unbreakable blocks!"));
            return false;
        }

        // Don't exchange with same block
        if (targetBlock == toPlace.block && targetMeta == toPlace.meta) {
            return false;
        }

        // Find all connected blocks of same type
        List<BlockPos> toExchange = findConnectedBlocks(world, x, y, z, targetBlock, targetMeta);

        if (toExchange.isEmpty()) {
            return false;
        }

        // TODO: Check if player has enough blocks
        // TODO: Drain blocks from inventory
        // TODO: Check alignment charge

        // Exchange all blocks
        int exchanged = 0;
        for (BlockPos pos : toExchange) {
            // Check if we can place here
            if (!player.canPlayerEdit(pos.x, pos.y, pos.z, side, stack)) {
                continue;
            }

            // Replace block
            world.setBlock(pos.x, pos.y, pos.z, toPlace.block, toPlace.meta, 3);
            exchanged++;
        }

        if (exchanged > 0) {
            // Play sound
            world.playSoundAtEntity(player, "random.anvil_use", 0.5F, 1.0F);

            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§a[Exchange Wand] Exchanged " + exchanged + " blocks"));
        }

        return true;

    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Right-click in air: cycle through stored blocks
        if (world.isRemote) {
            return stack;
        }

        Map<BlockAndMeta, ItemStack> stored = getMappedStoredStates(stack);
        if (stored.isEmpty()) {
            return stack;
        }

        // Cycle to next block
        int newIndex = ItemBlockStorage.cycleSelectedIndex(stack);
        BlockAndMeta selected = ItemBlockStorage.getSelectedBlock(stack);

        if (selected != null) {
            String blockName = selected.block.getLocalizedName();
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    "§6[Exchange Wand] §rSelected: §e" + blockName
                        + " §r("
                        + (newIndex + 1)
                        + "/"
                        + stored.size()
                        + ")"));
        }

        world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);

        return stack;

    }

    /**
     * Find all connected blocks of the same type
     * Uses flood-fill algorithm
     *
     * @param world       The world
     * @param startX      Start X position
     * @param startY      Start Y position
     * @param startZ      Start Z position
     * @param targetBlock The target block type
     * @param targetMeta  The target block metadata
     * @return List of positions to exchange
     */
    private List<BlockPos> findConnectedBlocks(World world, int startX, int startY, int startZ, Block targetBlock,
        int targetMeta) {
        List<BlockPos> found = new LinkedList<>();
        List<BlockPos> toCheck = new LinkedList<>();

        // Start with the clicked block
        toCheck.add(new BlockPos(startX, startY, startZ));

        // Track checked positions
        boolean[][][] checked = new boolean[SEARCH_DEPTH * 2 + 1][SEARCH_DEPTH * 2 + 1][SEARCH_DEPTH * 2 + 1];
        int offset = SEARCH_DEPTH;

        while (!toCheck.isEmpty() && found.size() < 1000) { // Limit to 1000 blocks
            BlockPos current = toCheck.remove(0);

            // Check bounds
            int dx = current.x - startX;
            int dy = current.y - startY;
            int dz = current.z - startZ;

            if (Math.abs(dx) > SEARCH_DEPTH || Math.abs(dy) > SEARCH_DEPTH || Math.abs(dz) > SEARCH_DEPTH) {
                continue;
            }

            // Check if already checked
            if (checked[dx + offset][dy + offset][dz + offset]) {
                continue;
            }
            checked[dx + offset][dy + offset][dz + offset] = true;

            // Check if block matches
            Block block = world.getBlock(current.x, current.y, current.z);
            int meta = world.getBlockMetadata(current.x, current.y, current.z);

            if (block == targetBlock && meta == targetMeta) {
                // Found matching block
                found.add(current);

                // Add neighbors to check
                toCheck.add(new BlockPos(current.x + 1, current.y, current.z));
                toCheck.add(new BlockPos(current.x - 1, current.y, current.z));
                toCheck.add(new BlockPos(current.x, current.y + 1, current.z));
                toCheck.add(new BlockPos(current.x, current.y - 1, current.z));
                toCheck.add(new BlockPos(current.x, current.y, current.z + 1));
                toCheck.add(new BlockPos(current.x, current.y, current.z - 1));
            }
        }

        return found;
    }

    /**
     * Helper class to store block positions
     */
    private static class BlockPos {

        public final int x, y, z;

        public BlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * NOTE: HUD Rendering
     * <p>
     * Original version:
     * - Renders stored blocks in HUD overlay
     * - Shows block icons and counts
     * - Displays available blocks from inventory
     * - Uses ItemHandRender and ItemHudRender interfaces
     * <p>
     * In 1.7.10:
     * - TODO: Implement HUD rendering
     * - TODO: Render stored block icons
     * - TODO: Display inventory counts
     * - TODO: Use Forge render events
     */

    /**
     * NOTE: Inventory Integration
     * <p>
     * Original version:
     * - Searches player inventory for blocks
     * - Shows count of available blocks
     * - Uses blocks from inventory first
     * - Can use Botania's bauble inventory
     * <p>
     * In 1.7.10:
     * - TODO: Implement inventory scanning
     * - TODO: Check available block count
     * - TODO: Drain blocks from inventory
     * - TODO: Support mod inventory integration
     */

    /**
     * NOTE: Block Discovery
     * <p>
     * Original version:
     * - Uses BlockDiscoverer.findConnected()
     * - Searches for connected blocks
     * - Respects search depth limit
     * - Can exclude certain blocks
     * <p>
     * In 1.7.10:
     * - Simplified flood-fill algorithm
     * - Fixed search depth (5 blocks)
     * - Checks block type and metadata
     * - Ignores unbreakable blocks
     */
}
