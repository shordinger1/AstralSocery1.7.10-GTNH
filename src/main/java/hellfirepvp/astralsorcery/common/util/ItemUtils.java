/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemUtils - Item utility methods
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * ItemUtils - Item utilities (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Item drop utilities</li>
 * <li>Ore dictionary checks</li>
 * <li>Item comparison utilities</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>ItemStack.EMPTY → null checks</li>
 * <li>getCount() → stackSize</li>
 * <li>Ore dictionary via OreDictionary class</li>
 * </ul>
 */
public class ItemUtils {

    /**
     * Drop item naturally at position with random offset
     * <p>
     * 1.7.10: Uses EntityItem constructor with random offset
     *
     * @param world The world
     * @param x     X position
     * @param y     Y position
     * @param z     Z position
     * @param stack Item stack to drop
     * @return The spawned EntityItem
     */
    public static EntityItem dropItemNaturally(World world, double x, double y, double z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }

        // Random offset for natural drop feel
        float f = world.rand.nextFloat() * 0.8F + 0.1F;
        float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
        float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, stack);

        // Delay before can pick up
        entityitem.delayBeforeCanPickup = 10;

        // Random motion
        entityitem.motionX = world.rand.nextGaussian() * 0.05;
        entityitem.motionY = world.rand.nextGaussian() * 0.05 + 0.2;
        entityitem.motionZ = world.rand.nextGaussian() * 0.05;

        world.spawnEntityInWorld(entityitem);
        return entityitem;
    }

    /**
     * Drop item at exact position (no offset)
     *
     * @param world The world
     * @param x     X position
     * @param y     Y position
     * @param z     Z position
     * @param stack Item stack to drop
     * @return The spawned EntityItem
     */
    public static EntityItem dropItemAt(World world, double x, double y, double z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }

        EntityItem entityitem = new EntityItem(world, x, y, z, stack);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
        return entityitem;
    }

    /**
     * Check if item stack has ore dictionary name
     * <p>
     * 1.7.10: Uses OreDictionary.getOreName()
     *
     * @param stack   Item stack to check
     * @param oreName Ore dictionary name to match
     * @return true if stack matches ore name
     */
    public static boolean hasOreName(ItemStack stack, String oreName) {
        if (stack == null || stack.stackSize <= 0) {
            return false;
        }

        int[] oreIds = net.minecraftforge.oredict.OreDictionary.getOreIDs(stack);
        for (int oreId : oreIds) {
            String name = net.minecraftforge.oredict.OreDictionary.getOreName(oreId);
            if (name.equals(oreName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if item is equal to another item (ignoring stack size)
     *
     * @param stack1 First item stack
     * @param stack2 Second item stack
     * @return true if items are the same
     */
    public static boolean isItemEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return false;
        }

        if (stack1.getItem() != stack2.getItem()) {
            return false;
        }

        // Check damage (for tools, items with durability, etc)
        if (stack1.getItemDamage() != stack2.getItemDamage()) {
            return false;
        }

        // Check NBT (for enchanted items, etc)
        if (!ItemStack.areItemStackTagsEqual(stack1, stack2)) {
            return false;
        }

        return true;
    }

    /**
     * Check if item stack is valid (not null and has items)
     *
     * @param stack Item stack to check
     * @return true if stack is valid
     */
    public static boolean isValid(ItemStack stack) {
        return stack != null && stack.stackSize > 0;
    }

    /**
     * Copy item stack with specified size
     *
     * @param stack Item stack to copy
     * @param size  New stack size
     * @return Copied stack with new size
     */
    public static ItemStack copyStackWithSize(ItemStack stack, int size) {
        if (stack == null) {
            return null;
        }
        ItemStack copy = stack.copy();
        copy.stackSize = size;
        return copy;
    }

    /**
     * Get item stack size safely (returns 0 if null)
     *
     * @param stack Item stack
     * @return Stack size or 0
     */
    public static int getStackSize(ItemStack stack) {
        return stack != null ? stack.stackSize : 0;
    }
}
