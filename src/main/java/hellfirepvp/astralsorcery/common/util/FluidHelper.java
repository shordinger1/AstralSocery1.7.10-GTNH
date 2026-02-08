/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * FluidHelper - Fluid handling utilities for 1.7.10
 *
 * Provides helper methods for fluid container operations.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/**
 * FluidHelper - Fluid handling utilities for 1.7.10
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Check if item contains fluid</li>
 * <li>Get fluid from container item</li>
 * <li>Drain fluid from container item</li>
 * <li>Fill fluid into container item</li>
 * <li>Get empty container from filled container</li>
 * </ul>
 * <p>
 * <b>1.7.10 Forge Fluid API:</b>
 * <ul>
 * <li>FluidContainerRegistry - Global fluid container registry</li>
 * <li>IFluidContainerItem - Interface for fluid containers</li>
 * <li>FluidStack - Stack of fluid with amount</li>
 * </ul>
 * <p>
 * <b>Usage Examples:</b>
 *
 * <pre>
 * 
 * // Check if item has fluid
 * boolean hasFluid = FluidHelper.hasFluid(stack);
 *
 * // Get fluid from item
 * FluidStack fluid = FluidHelper.getFluid(stack);
 *
 * // Drain fluid from item
 * FluidStack drained = FluidHelper.drain(stack, amount);
 *
 * // Get empty container
 * ItemStack empty = FluidHelper.getEmptyContainer(stack);
 * </pre>
 */
public class FluidHelper {

    /**
     * Check if an item stack contains fluid
     *
     * @param stack The item stack to check
     * @return true if the item contains fluid
     */
    public static boolean hasFluid(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return false;
        }

        // Check IFluidContainerItem interface
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) stack.getItem();
            return container.getFluid(stack) != null;
        }

        // Check FluidContainerRegistry
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
        return fluid != null && fluid.amount > 0;
    }

    /**
     * Get the fluid stack from an item
     *
     * @param stack The item stack
     * @return The fluid stack, or null if no fluid
     */
    public static FluidStack getFluid(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }

        // Try IFluidContainerItem first
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) stack.getItem();
            return container.getFluid(stack);
        }

        // Fall back to FluidContainerRegistry
        return FluidContainerRegistry.getFluidForFilledItem(stack);
    }

    /**
     * Get the fluid type from an item (without amount)
     *
     * @param stack The item stack
     * @return The fluid, or null if no fluid
     */
    public static Fluid getFluidType(ItemStack stack) {
        FluidStack fluidStack = getFluid(stack);
        return fluidStack != null ? fluidStack.getFluid() : null;
    }

    /**
     * Check if an item contains a specific fluid
     *
     * @param stack        The item stack
     * @param fluidToCheck The fluid to check for
     * @return true if the item contains the specified fluid
     */
    public static boolean containsFluid(ItemStack stack, Fluid fluidToCheck) {
        Fluid fluid = getFluidType(stack);
        return fluid != null && fluid.equals(fluidToCheck);
    }

    /**
     * Drain fluid from a container item
     * <p>
     * Returns the drained fluid and a new item stack with the fluid removed.
     * This does NOT modify the original stack.
     *
     * @param stack  The container item
     * @param amount Amount to drain (in mB)
     * @return ContainerDrainResult containing drained fluid and remaining item
     */
    public static ContainerDrainResult drain(ItemStack stack, int amount) {
        if (stack == null || stack.stackSize <= 0) {
            return new ContainerDrainResult(null, stack);
        }

        // Try IFluidContainerItem first
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) stack.getItem();
            FluidStack drained = container.drain(stack, amount, true);

            if (drained != null && drained.amount > 0) {
                // Create copy with fluid drained
                ItemStack result = stack.copy();
                result.stackSize = 1;
                container.drain(result, amount, true);
                return new ContainerDrainResult(drained, result);
            }
        }

        // Fall back to FluidContainerRegistry
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
        if (fluid != null) {
            int drainAmount = Math.min(amount, fluid.amount);
            FluidStack drained = new FluidStack(fluid, drainAmount);

            // Get empty container - in 1.7.10, drainFluidContainer only takes ItemStack
            ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(stack);
            if (emptyContainer != null) {
                return new ContainerDrainResult(drained, emptyContainer);
            }
        }

        return new ContainerDrainResult(null, stack);
    }

    /**
     * Get the empty container version of a filled container
     *
     * @param filledContainer The filled container item
     * @return The empty container item, or null if not found
     */
    public static ItemStack getEmptyContainer(ItemStack filledContainer) {
        if (filledContainer == null || filledContainer.stackSize <= 0) {
            return null;
        }

        // Try IFluidContainerItem first
        if (filledContainer.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) filledContainer.getItem();

            // Create a copy to check capacity
            ItemStack copy = filledContainer.copy();
            copy.stackSize = 1;
            FluidStack drained = container.drain(copy, Integer.MAX_VALUE, false);

            if (drained != null && drained.amount > 0) {
                // Actually drain to get empty container
                container.drain(copy, Integer.MAX_VALUE, true);
                return copy;
            }
        }

        // Fall back to FluidContainerRegistry
        // In 1.7.10, we need to get the fluid first and then drain
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
        if (fluid != null) {
            // Use drainFluidContainer with correct signature
            ItemStack drained = FluidContainerRegistry.drainFluidContainer(filledContainer);
            if (drained != null) {
                return drained;
            }
        }

        return null;
    }

    /**
     * Get the filled container version of an empty container
     *
     * @param emptyContainer The empty container item
     * @param fluid          The fluid to fill with
     * @param amount         Amount to fill (in mB)
     * @return The filled container item, or null if cannot fill
     */
    public static ItemStack getFilledContainer(ItemStack emptyContainer, Fluid fluid, int amount) {
        if (emptyContainer == null || emptyContainer.stackSize <= 0 || fluid == null) {
            return null;
        }

        // Try IFluidContainerItem first
        if (emptyContainer.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) emptyContainer.getItem();

            // Create a copy to fill
            ItemStack copy = emptyContainer.copy();
            copy.stackSize = 1;
            int filled = container.fill(copy, new FluidStack(fluid, amount), true);

            if (filled > 0) {
                return copy;
            }
        }

        // Fall back to FluidContainerRegistry
        // In 1.7.10, the signature is fillFluidContainer(FluidStack, ItemStack)
        FluidStack fluidStack = new FluidStack(fluid, amount);
        return FluidContainerRegistry.fillFluidContainer(fluidStack, emptyContainer);
    }

    /**
     * Check if a container can be filled with a specific fluid
     *
     * @param emptyContainer The empty container
     * @param fluid          The fluid to check
     * @return true if the container can be filled with the fluid
     */
    public static boolean canFill(ItemStack emptyContainer, Fluid fluid) {
        if (emptyContainer == null || fluid == null) {
            return false;
        }

        // Try IFluidContainerItem first
        if (emptyContainer.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) emptyContainer.getItem();
            ItemStack copy = emptyContainer.copy();
            copy.stackSize = 1;
            return container.fill(copy, new FluidStack(fluid, 1), false) > 0;
        }

        // Fall back to FluidContainerRegistry
        // In 1.7.10, we check if there's a filled container for this fluid
        FluidStack fluidStack = new FluidStack(fluid, 1);
        ItemStack filled = FluidContainerRegistry.fillFluidContainer(fluidStack, emptyContainer);
        return filled != null;
    }

    /**
     * Get the fluid capacity of a container
     *
     * @param container The container item
     * @param fluid     The fluid to check capacity for
     * @return Capacity in mB, or 0 if not a fluid container
     */
    public static int getCapacity(ItemStack container, Fluid fluid) {
        if (container == null || container.stackSize <= 0 || fluid == null) {
            return 0;
        }

        // Try IFluidContainerItem first
        if (container.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem contItem = (IFluidContainerItem) container.getItem();
            return contItem.getCapacity(container);
        }

        // Fall back to FluidContainerRegistry
        // In 1.7.10, getContainerCapacity requires ItemStack, not Fluid
        FluidStack fluidStack = new FluidStack(fluid, 1);
        // Try to fill and check amount
        ItemStack filled = FluidContainerRegistry.fillFluidContainer(fluidStack, container);
        if (filled != null) {
            FluidStack resultFluid = FluidContainerRegistry.getFluidForFilledItem(filled);
            if (resultFluid != null) {
                return resultFluid.amount;
            }
        }

        return 0;
    }

    /**
     * Result of draining a container
     */
    public static class ContainerDrainResult {

        /** The drained fluid stack */
        public final FluidStack fluid;

        /** The item stack after draining (may be empty container) */
        public final ItemStack resultingItem;

        public ContainerDrainResult(FluidStack fluid, ItemStack resultingItem) {
            this.fluid = fluid;
            this.resultingItem = resultingItem;
        }

        /**
         * Check if the drain was successful
         *
         * @return true if fluid was drained
         */
        public boolean isSuccess() {
            return fluid != null && fluid.amount > 0;
        }
    }
}
