/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Architect Wand - Place blocks remotely
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage.BlockAndMeta;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Architect Wand
 * <p>
 * A wand that can place blocks remotely.
 * <p>
 * Features:
 * - Store block types by sneaking + clicking
 * - Place blocks remotely (up to 60 blocks away)
 * - Cycle through stored blocks
 * - Display stored blocks in HUD (TODO)
 * <p>
 * Controls:
 * - Sneak + Click block: Store block
 * - Click block: Place stored block
 * - Right-click air: Cycle stored blocks
 * <p>
 * TODO:
 * - Implement HUD rendering (show stored blocks)
 * - Implement block preview rendering
 * - Implement range checking
 * - Implement alignment charge system
 * - Implement particle effects
 * - Implement sound effects
 */
public class ItemArchitectWand extends ItemBlockStorage {

    private static final double ARCHITECT_RANGE = 60.0D;

    public ItemArchitectWand() {
        super();
        setMaxStackSize(1); // Only one wand per stack
        setMaxDamage(0); // No durability - infinite use
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @Override
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
                new net.minecraft.util.ChatComponentText(color + "[Architect Wand] " + "Stored blocks: " + count));

            world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);
            return true;
        }

        // Not sneaking: Place stored block
        Map<BlockAndMeta, ItemStack> stored = getMappedStoredStates(stack);
        if (stored.isEmpty()) {
            player.addChatMessage(
                new ChatComponentText("§c[Architect Wand] No blocks stored! Sneak + Click to store blocks."));
            return false;
        }

        // Get the currently selected block
        BlockAndMeta toPlace = ItemBlockStorage.getSelectedBlock(stack);
        if (toPlace == null) {
            player.addChatMessage(new ChatComponentText("§c[Architect Wand] No block selected!"));
            return false;
        }

        // Adjust position based on side
        int newX = x;
        int newY = y;
        int newZ = z;
        switch (side) {
            case 0:
                newY--;
                break; // Bottom
            case 1:
                newY++;
                break; // Top
            case 2:
                newZ--;
                break; // North
            case 3:
                newZ++;
                break; // South
            case 4:
                newX--;
                break; // West
            case 5:
                newX++;
                break; // East
        }

        // Check if player can place here
        if (!player.canPlayerEdit(newX, newY, newZ, side, stack)) {
            return false;
        }

        // Check if we can place here (replacable and air)
        Block blockAt = world.getBlock(newX, newY, newZ);
        if (!blockAt.isReplaceable(world, newX, newY, newZ)) {
            return false;
        }

        // Place the block
        // TODO: Check if player has enough blocks in inventory
        // TODO: Drain blocks from inventory
        // TODO: Check alignment charge
        world.setBlock(newX, newY, newZ, toPlace.block, toPlace.meta, 3);

        // Play sound
        world.playSoundEffect(
            newX + 0.5,
            newY + 0.5,
            newZ + 0.5,
            toPlace.block.stepSound.soundName,
            (toPlace.block.stepSound.getVolume() + 1.0F) / 2.0F,
            toPlace.block.stepSound.getPitch() * 0.8F);

        return true;
    }

    @Override
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
                new ChatComponentText(
                    "§6[Architect Wand] §rSelected: §e" + blockName
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
     * NOTE: Range Checking
     * <p>
     * Original version:
     * - Max range: 60 blocks
     * - Raytrace to find target position
     * - Shows preview of block placement
     * <p>
     * In 1.7.10:
     * - TODO: Implement raytrace
     * - TODO: Implement range check
     * - TODO: Implement block preview
     * - TODO: Show outline at target position
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
}
