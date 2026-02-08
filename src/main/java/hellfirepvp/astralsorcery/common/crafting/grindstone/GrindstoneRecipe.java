/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Grindstone Recipe - Grinds items into dust
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import java.util.Random;

import net.minecraft.item.ItemStack;

/**
 * GrindstoneRecipe - Grindstone recipe (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Input item matching</li>
 * <li>Output item with chance</li>
 * <li>Optional double output chance</li>
 * <li>Recipe results (success, item change, fail)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Uses ItemStack.EMPTY → null for empty items</li>
 * <li>ItemStack.stackSize instead of getCount()</li>
 * <li>Simple item matching</li>
 * </ul>
 */
public class GrindstoneRecipe {

    protected static final Random rand = new Random();

    protected final ItemStack input;
    protected final ItemStack output;
    protected final int chance;
    protected final float doubleChance;

    /**
     * Create a grindstone recipe
     *
     * @param input  Input item (will be copied)
     * @param output Output item (will be copied)
     * @param chance 1 in X chance to produce output
     */
    public GrindstoneRecipe(ItemStack input, ItemStack output, int chance) {
        this(input, output, chance, 0F);
    }

    /**
     * Create a grindstone recipe with double output chance
     *
     * @param input        Input item (will be copied)
     * @param output       Output item (will be copied)
     * @param chance       1 in X chance to produce output
     * @param doubleChance Chance (0-1) to double output
     */
    public GrindstoneRecipe(ItemStack input, ItemStack output, int chance, float doubleChance) {
        // Copy input and output to prevent modification
        if (input != null) {
            this.input = input.copy();
        } else {
            this.input = null;
        }

        if (output != null) {
            this.output = output.copy();
        } else {
            this.output = null;
        }

        this.chance = chance;
        this.doubleChance = Math.max(0, Math.min(1, doubleChance)); // Clamp 0-1
    }

    /**
     * Check if this recipe matches the input stack
     *
     * @param stackIn Input stack to check
     * @return true if matches
     */
    public boolean matches(ItemStack stackIn) {
        if (stackIn == null || input == null) {
            return false;
        }

        // Check item and damage
        if (stackIn.getItem() != input.getItem()) {
            return false;
        }

        // Check damage value (metadata)
        if (stackIn.getItemDamage() != input.getItemDamage()) {
            return false;
        }

        return true;
    }

    /**
     * Check if this recipe is valid
     *
     * @return true if valid
     */
    public boolean isValid() {
        return input != null && output != null;
    }

    /**
     * Get chance to double output (0-1)
     *
     * @return Double output chance
     */
    public float getChanceToDoubleOutput() {
        return Math.max(0, Math.min(1, this.doubleChance));
    }

    /**
     * Grind the input item
     *
     * @param stackIn Input stack (not modified)
     * @return Grinding result
     */
    public GrindResult grind(ItemStack stackIn) {
        if (rand.nextInt(chance) == 0) {
            int out = this.output.stackSize;

            // Check for double output
            if (rand.nextFloat() <= getChanceToDoubleOutput()) {
                out *= 2;
            }

            // Create output stack
            ItemStack result = this.output.copy();
            result.stackSize = out;
            return GrindResult.itemChange(result);
        }

        return GrindResult.failNoOp();
    }

    /**
     * Get output item for display
     *
     * @return Output item copy
     */
    public ItemStack getOutputForMatching() {
        if (output != null) {
            return output.copy();
        }
        return null;
    }

    /**
     * Get input item for display
     *
     * @return Input item copy
     */
    public ItemStack getInputForRender() {
        if (input != null) {
            return input.copy();
        }
        return null;
    }

    /**
     * Grinding result
     */
    public static class GrindResult {

        private final ResultType type;
        private final ItemStack stack;

        private GrindResult(ResultType type, ItemStack stack) {
            this.type = type;
            this.stack = stack;
        }

        public ResultType getType() {
            return type;
        }

        public ItemStack getStack() {
            return stack;
        }

        /**
         * Success with no item change
         */
        public static GrindResult success() {
            return new GrindResult(ResultType.SUCCESS, null);
        }

        /**
         * Success with item change
         *
         * @param newStack New item stack
         */
        public static GrindResult itemChange(ItemStack newStack) {
            return new GrindResult(ResultType.ITEMCHANGE, newStack);
        }

        /**
         * Failed silently (no operation)
         */
        public static GrindResult failNoOp() {
            return new GrindResult(ResultType.FAIL_SILENT, null);
        }

        /**
         * Failed and broke the item
         */
        public static GrindResult failBreakItem() {
            return new GrindResult(ResultType.FAIL_BREAK_ITEM, null);
        }
    }

    /**
     * Grinding result type
     */
    public static enum ResultType {

        /** Successfully grinded something */
        SUCCESS,
        /** Successfully grinded something, other item now on the grindstone */
        ITEMCHANGE,
        /** Did nothing (failed silently) */
        FAIL_SILENT,
        /** The item broke while grinding */
        FAIL_BREAK_ITEM
    }
}
