/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemStack utility class for API compatibility
 *
 * This class provides compatibility methods between 1.12.2 and 1.7.10 ItemStack APIs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.item.ItemStack;

/**
 * ItemStackUtils - Utility class for ItemStack operations
 * <p>
 * <b>Purpose</b>: Provides 1.12.2-like API methods for 1.7.10 ItemStack handling
 * <p>
 * <b>Key Differences</b>:
 * <ul>
 * <li>1.12.2 uses ItemStack.EMPTY for empty stacks</li>
 * <li>1.7.10 uses null for empty stacks</li>
 * <li>1.12.2 uses stack.isEmpty() method</li>
 * <li>1.7.10 requires null/size checks</li>
 * </ul>
 * <p>
 * <b>Usage</b>:
 * 
 * <pre>
 * if (ItemStackUtils.isEmpty(stack)) {
 *     // Stack is empty
 * }
 * int count = ItemStackUtils.getStackSize(stack);
 * </pre>
 */
public class ItemStackUtils {

    /**
     * Check if an ItemStack is empty (null or stackSize <= 0)
     * <p>
     * 1.12.2 equivalent: stack.isEmpty()
     *
     * @param stack The ItemStack to check
     * @return true if the stack is null or has no items
     */
    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.stackSize <= 0;
    }

    /**
     * Check if an ItemStack is not empty
     *
     * @param stack The ItemStack to check
     * @return true if the stack has items
     */
    public static boolean notEmpty(ItemStack stack) {
        return stack != null && stack.stackSize > 0;
    }

    /**
     * Get an empty ItemStack (null in 1.7.10)
     * <p>
     * 1.12.2 equivalent: ItemStack.EMPTY
     *
     * @return null (representing empty stack)
     */
    public static ItemStack getEmpty() {
        return null;
    }

    /**
     * Copy an ItemStack safely
     * <p>
     * 1.12.2 equivalent: stack.copy()
     *
     * @param stack The ItemStack to copy
     * @return A copy of the stack, or null if stack is null
     */
    public static ItemStack copy(ItemStack stack) {
        return stack != null ? stack.copy() : null;
    }

    /**
     * Copy an ItemStack with a specific size
     * <p>
     * 1.12.2 equivalent: ItemStack.copyStackWithSize(stack, size)
     *
     * @param stack The ItemStack to copy
     * @param size  The new stack size
     * @return A copy of the stack with the specified size
     */
    public static ItemStack copyWithSize(ItemStack stack, int size) {
        if (stack == null) {
            return null;
        }
        ItemStack copy = stack.copy();
        copy.stackSize = size;
        return copy;
    }

    /**
     * Get the size of an ItemStack
     * <p>
     * 1.12.2 equivalent: stack.getCount()
     *
     * @param stack The ItemStack to check
     * @return The stack size, or 0 if stack is null
     */
    public static int getStackSize(ItemStack stack) {
        return stack != null ? stack.stackSize : 0;
    }

    /**
     * Set the size of an ItemStack
     * <p>
     * 1.12.2 equivalent: stack.setCount(size)
     *
     * @param stack The ItemStack to modify
     * @param size  The new stack size
     */
    public static void setStackSize(ItemStack stack, int size) {
        if (stack != null) {
            stack.stackSize = size;
        }
    }

    /**
     * Shrink an ItemStack by a specified amount
     * <p>
     * 1.12.2 equivalent: stack.shrink(amount)
     *
     * @param stack  The ItemStack to shrink
     * @param amount The amount to shrink by
     * @return The shrunk stack, or null if size becomes 0 or less
     */
    public static ItemStack shrink(ItemStack stack, int amount) {
        if (stack == null) {
            return null;
        }
        stack.stackSize -= amount;
        if (stack.stackSize <= 0) {
            return null;
        }
        return stack;
    }

    /**
     * Grow an ItemStack by a specified amount
     * <p>
     * 1.12.2 equivalent: stack.grow(amount)
     *
     * @param stack  The ItemStack to grow
     * @param amount The amount to grow by
     * @return The same stack (modified)
     */
    public static ItemStack grow(ItemStack stack, int amount) {
        if (stack != null) {
            stack.stackSize += amount;
        }
        return stack;
    }

    /**
     * Check if two ItemStacks are equal (item and metadata)
     * <p>
     * Uses ItemStack.areItemStacksEqual() internally
     *
     * @param stack1 First ItemStack
     * @param stack2 Second ItemStack
     * @return true if the stacks are equal
     */
    public static boolean areEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null && stack2 == null) {
            return true;
        }
        if (stack1 == null || stack2 == null) {
            return false;
        }
        return stack1.isItemEqual(stack2);
    }

    /**
     * Check if two ItemStacks are equal including stack size
     *
     * @param stack1 First ItemStack
     * @param stack2 Second ItemStack
     * @return true if the stacks are completely equal
     */
    public static boolean areEqualStrict(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null && stack2 == null) {
            return true;
        }
        if (stack1 == null || stack2 == null) {
            return false;
        }
        return ItemStack.areItemStacksEqual(stack1, stack2);
    }

    /**
     * Get the display name of an ItemStack safely
     *
     * @param stack The ItemStack
     * @return The display name, or "Empty" if stack is null
     */
    public static String getDisplayName(ItemStack stack) {
        if (isEmpty(stack)) {
            return "Empty";
        }
        return stack.getDisplayName();
    }

    /**
     * Validate an ItemStack array, removing null/empty entries
     *
     * @param stacks The ItemStack array to validate
     * @return The number of non-empty stacks
     */
    public static int validateArray(ItemStack[] stacks) {
        if (stacks == null) {
            return 0;
        }
        int count = 0;
        for (ItemStack stack : stacks) {
            if (notEmpty(stack)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compare ItemStacks for recipe matching
     * <p>
     * Checks if two stacks match for crafting purposes (item, metadata, ignore NBT)
     *
     * @param input    The input stack
     * @param required The required stack
     * @return true if the stacks match for crafting
     */
    public static boolean matchForCrafting(ItemStack input, ItemStack required) {
        if (isEmpty(input) && isEmpty(required)) {
            return true;
        }
        if (isEmpty(input) || isEmpty(required)) {
            return false;
        }
        return input.getItem() == required.getItem() && input.getItemDamage() == required.getItemDamage();
    }

    /**
     * Compare ItemStacks for recipe matching with NBT
     *
     * @param input    The input stack
     * @param required The required stack
     * @return true if the stacks match including NBT
     */
    public static boolean matchStrict(ItemStack input, ItemStack required) {
        if (isEmpty(input) && isEmpty(required)) {
            return true;
        }
        if (isEmpty(input) || isEmpty(required)) {
            return false;
        }
        return ItemStack.areItemStacksEqual(input, required);
    }
}
