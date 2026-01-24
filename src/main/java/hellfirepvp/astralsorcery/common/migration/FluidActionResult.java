/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.item.ItemStack;

/**
 * Migration class for FluidActionResult (introduced in Minecraft 1.12+).
 * Wraps the result ItemStack from a fluid interaction operation.
 */
public class FluidActionResult {

    private final ItemStack result;

    /**
     * Creates a FluidActionResult with the given result ItemStack.
     *
     * @param result the resulting ItemStack from the fluid operation
     */
    public FluidActionResult(ItemStack result) {
        this.result = result;
    }

    /**
     * Gets the result ItemStack.
     *
     * @return the result ItemStack
     */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Returns whether the operation was successful.
     * In 1.12+, this checks if the result is not empty.
     * For 1.7.10 compatibility, we check if the stack is not null and has items.
     *
     * @return true if the operation was successful
     */
    public boolean isSuccess() {
        return result != null && result.stackSize > 0;
    }
}
