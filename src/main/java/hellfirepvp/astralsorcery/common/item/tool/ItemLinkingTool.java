/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Linking Tool - Tool for linking blocks in starlight network
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Linking Tool
 * <p>
 * A tool used to link blocks in the starlight network.
 * <p>
 * Features:
 * - Links two blocks together
 * - Sneak + click to select first block
 * - Click to select second block and link
 * - Visual feedback with particles (TODO)
 * <p>
 * Controls:
 * - Sneak + Right-click block: Set as source
 * - Right-click block: Set as destination and create link
 * <p>
 * TODO:
 * - Implement LinkHandler system
 * - Implement link storage (NBT)
 * - Implement link validation
 * - Implement particle effects for links
 * - Implement link breaking
 */
public class ItemLinkingTool extends AstralBaseItem {

    private static final String TAG_LINK_X = "link_x";
    private static final String TAG_LINK_Y = "link_y";
    private static final String TAG_LINK_Z = "link_z";
    private static final String TAG_LINK_DIM = "link_dim";

    public ItemLinkingTool() {
        super();
        setMaxStackSize(1);
        setMaxDamage(0);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        boolean isSneaking = player.isSneaking();

        if (isSneaking) {
            // Set link source
            setLinkSource(stack, world.provider.dimensionId, x, y, z);
            player.addChatMessage(new ChatComponentText("§a[Linking Tool] §rSource set: §e" + x + ", " + y + ", " + z));
            world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);
        } else {
            // Try to link to destination
            if (hasLinkSource(stack)) {
                int[] source = getLinkSource(stack);

                // TODO: Validate link
                // TODO: Create link between blocks

                player.addChatMessage(
                    new ChatComponentText(
                        "§a[Linking Tool] §rLink created: §e" + source[0]
                            + ", "
                            + source[1]
                            + ", "
                            + source[2]
                            + " §r→ §e"
                            + x
                            + ", "
                            + y
                            + ", "
                            + z));

                // Clear source after linking
                clearLinkSource(stack);

                world.playSoundAtEntity(player, "random.anvil_use", 0.5F, 1.0F);
            } else {
                player.addChatMessage(
                    new ChatComponentText("§c[Linking Tool] No source set! Sneak + click to set source."));
            }
        }

        return true;

    }

    /**
     * Set link source coordinates
     */
    private void setLinkSource(ItemStack stack, int dimension, int x, int y, int z) {
        setNBT(stack, TAG_LINK_DIM, dimension);
        setNBT(stack, TAG_LINK_X, x);
        setNBT(stack, TAG_LINK_Y, y);
        setNBT(stack, TAG_LINK_Z, z);
    }

    /**
     * Get link source coordinates
     * Returns [dimension, x, y, z] or null if not set
     */
    private int[] getLinkSource(ItemStack stack) {
        if (!hasLinkSource(stack)) {
            return null;
        }
        return new int[] { getNBTInt(stack, TAG_LINK_DIM), getNBTInt(stack, TAG_LINK_X), getNBTInt(stack, TAG_LINK_Y),
            getNBTInt(stack, TAG_LINK_Z) };
    }

    /**
     * Check if has link source
     */
    private boolean hasLinkSource(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound()
            .hasKey(TAG_LINK_X);
    }

    /**
     * Clear link source
     */
    private void clearLinkSource(ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound()
                .removeTag(TAG_LINK_X);
            stack.getTagCompound()
                .removeTag(TAG_LINK_Y);
            stack.getTagCompound()
                .removeTag(TAG_LINK_Z);
            stack.getTagCompound()
                .removeTag(TAG_LINK_DIM);
        }
    }

    /**
     * Helper to set NBT integer
     */
    private void setNBT(ItemStack stack, String key, int value) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        stack.getTagCompound()
            .setInteger(key, value);
    }

    /**
     * Helper to get NBT integer
     */
    private int getNBTInt(ItemStack stack, String key) {
        return stack.getTagCompound()
            .getInteger(key);
    }

    /**
     * NOTE: LinkHandler System
     * <p>
     * Original version:
     * - LinkHandler manages all block links
     * - Validates links based on distance and type
     * - Stores links in world data
     * - Provides link lookup functionality
     * <p>
     * In 1.7.10:
     * - TODO: Implement LinkHandler class
     * - TODO: Implement link storage system
     * - TODO: Implement link validation
     * - TODO: Implement packet system for syncing
     */
}
