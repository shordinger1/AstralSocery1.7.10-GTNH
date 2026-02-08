/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * FluidAltarRecipe - Fluid container recipe helper
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * Helper class for creating fluid container altar recipes
 * <p>
 * <b>Usage:</b>
 *
 * <pre>
 * // Create a recipe that uses a water bucket
 * ItemStack waterBucket = FluidContainerRegistry.getFilledContainer(new FluidStack(FluidRegistry.WATER, 1000));
 * ItemStack output = new ItemStack(Items.ice);
 *
 * ASAltarRecipe recipe = FluidAltarRecipe.create(
 *     TileAltar.AltarLevel.DISCOVERY,
 *     waterBucket,
 *     output,
 *     "aevitas", // constellation
 *     50, // starlight required
 *     200 // crafting time
 * );
 *
 * // Register the recipe
 * AltarRecipeRegistry.addRecipe(recipe);
 * </pre>
 */
public class FluidAltarRecipe {

    /**
     * Create a simple fluid container recipe
     * <p>
     * The fluid container will be drained and replaced with its empty version.
     *
     * @param altarLevel        The altar level required
     * @param fluidContainer    The filled fluid container (e.g., water bucket)
     * @param output            The output item
     * @param constellation     Optional constellation required (can be null)
     * @param starlightRequired Starlight required for crafting
     * @param craftingTime      Time in ticks to craft
     * @return The created recipe
     */
    public static ASAltarRecipe create(TileAltar.AltarLevel altarLevel, ItemStack fluidContainer, ItemStack output,
        String constellation, int starlightRequired, int craftingTime) {

        // Create a single-input recipe with the fluid container
        ItemStack[] inputs = new ItemStack[] { fluidContainer };

        return new ASAltarRecipe(
            altarLevel,
            inputs,
            output,
            constellation,
            starlightRequired,
            craftingTime,
            false, // Not shaped (single slot)
            1, // Width
            1 // Height
        );
    }

    /**
     * Create a fluid container recipe with additional items
     * <p>
     * Example: Water Bucket + Sand + Sand = Glass
     *
     * @param altarLevel        The altar level required
     * @param fluidContainer    The filled fluid container
     * @param additionalItems   Additional input items
     * @param output            The output item
     * @param constellation     Optional constellation required
     * @param starlightRequired Starlight required
     * @param craftingTime      Time in ticks
     * @param shaped            Whether recipe is shaped
     * @param width             Recipe width (if shaped)
     * @param height            Recipe height (if shaped)
     * @return The created recipe
     */
    public static ASAltarRecipe createWithItems(TileAltar.AltarLevel altarLevel, ItemStack fluidContainer,
        ItemStack[] additionalItems, ItemStack output, String constellation, int starlightRequired, int craftingTime,
        boolean shaped, int width, int height) {

        // Combine fluid container with additional items
        ItemStack[] inputs = new ItemStack[1 + additionalItems.length];
        inputs[0] = fluidContainer;
        System.arraycopy(additionalItems, 0, inputs, 1, additionalItems.length);

        return new ASAltarRecipe(
            altarLevel,
            inputs,
            output,
            constellation,
            starlightRequired,
            craftingTime,
            shaped,
            width,
            height);
    }

    /**
     * Create a shaped fluid recipe
     * <p>
     * Example 3x3 recipe:
     * 
     * <pre>
     * W S I
     * S O S
     * I S W
     * Where W = Water bucket, S = Sand, I = Iron ingot, O = Obsidian
     * </pre>
     *
     * @param altarLevel        The altar level required
     * @param fluidContainer    The filled fluid container
     * @param patternItems      Items in pattern (excluding nulls)
     * @param output            The output item
     * @param constellation     Optional constellation required
     * @param starlightRequired Starlight required
     * @param craftingTime      Time in ticks
     * @param width             Recipe width
     * @param height            Recipe height
     * @return The created recipe
     */
    public static ASAltarRecipe createShaped(TileAltar.AltarLevel altarLevel, ItemStack fluidContainer,
        ItemStack[] patternItems, ItemStack output, String constellation, int starlightRequired, int craftingTime,
        int width, int height) {

        // Prepend fluid container to pattern
        ItemStack[] inputs = new ItemStack[1 + patternItems.length];
        inputs[0] = fluidContainer;
        System.arraycopy(patternItems, 0, inputs, 1, patternItems.length);

        return new ASAltarRecipe(
            altarLevel,
            inputs,
            output,
            constellation,
            starlightRequired,
            craftingTime,
            true, // Shaped
            width,
            height);
    }

    /**
     * Get a filled fluid container item
     * <p>
     * Convenience method for getting filled containers from Forge FluidRegistry
     * In 1.7.10, we try to fill an empty bucket with the fluid
     *
     * @param fluidName The name of the fluid (e.g., "water", "lava", "astralsorcery.liquidstarlight")
     * @param amount    Amount in mB (typically 1000 for a bucket)
     * @return The filled container item, or null if not found
     */
    public static ItemStack getFilledContainer(String fluidName, int amount) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            return null;
        }

        FluidStack fluidStack = new FluidStack(fluid, amount);

        // In 1.7.10, we try to fill an empty bucket with the fluid
        ItemStack emptyBucket = new ItemStack(net.minecraft.init.Items.bucket);
        ItemStack filled = FluidContainerRegistry.fillFluidContainer(fluidStack, emptyBucket);

        return filled;
    }

    /**
     * Get a filled fluid container item with default bucket amount (1000 mB)
     *
     * @param fluidName The name of the fluid
     * @return The filled container item, or null if not found
     */
    public static ItemStack getFilledBucket(String fluidName) {
        return getFilledContainer(fluidName, 1000);
    }

    /**
     * Create a recipe using a fluid bucket
     * <p>
     * Shortcut for creating recipes with standard bucket amounts
     *
     * @param altarLevel        The altar level required
     * @param fluidName         The name of the fluid
     * @param output            The output item
     * @param constellation     Optional constellation required
     * @param starlightRequired Starlight required
     * @param craftingTime      Time in ticks
     * @return The created recipe, or null if fluid not found
     */
    public static ASAltarRecipe createFromBucket(TileAltar.AltarLevel altarLevel, String fluidName, ItemStack output,
        String constellation, int starlightRequired, int craftingTime) {

        ItemStack bucket = getFilledBucket(fluidName);
        if (bucket == null) {
            return null;
        }

        return create(altarLevel, bucket, output, constellation, starlightRequired, craftingTime);
    }

    /**
     * Validate that a recipe input is a fluid container
     *
     * @param stack The item stack to check
     * @return true if the stack is a filled fluid container
     */
    public static boolean isValidFluidContainer(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return false;
        }

        // Check FluidContainerRegistry
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
        return fluid != null && fluid.amount > 0;
    }

    /**
     * Validate that a recipe input contains a specific fluid
     *
     * @param stack     The item stack to check
     * @param fluidName The expected fluid name
     * @return true if the stack contains the specified fluid
     */
    public static boolean containsFluid(ItemStack stack, String fluidName) {
        if (stack == null || stack.stackSize <= 0) {
            return false;
        }

        Fluid expectedFluid = FluidRegistry.getFluid(fluidName);
        if (expectedFluid == null) {
            return false;
        }

        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
        return fluid != null && fluid.getFluid() == expectedFluid;
    }
}
