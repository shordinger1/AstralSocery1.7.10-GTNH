/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASAltarRecipe - Altar recipe definition
 *
 * 1.7.10: Simplified from 1.12.2 version
 * - Removed Crafttweaker integration
 * - Simplified matching logic
 * - Direct item comparison
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * Base altar recipe class
 * <p>
 * Defines a crafting recipe for an altar. Recipes are created by passing
 * input items and optional constellation, and produce an output item.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * <ul>
 * <li>No Crafttweaker integration</li>
 * <li>Simplified matching logic</li>
 * <li>Direct item/oreDict comparison</li>
 * <li>No advancement instances</li>
 * </ul>
 */
public class ASAltarRecipe {

    private final TileAltar.AltarLevel altarLevel;
    private final ItemStack[] inputs;
    private final ItemStack output;
    private final String constellation; // Optional constellation requirement
    private final int starlightRequired;
    private final int craftingTime;

    // Phase 4: Shaped recipe support
    private final boolean shaped; // True if recipe is shaped (ordered)
    private final int width; // Recipe width (for shaped recipes)
    private final int height; // Recipe height (for shaped recipes)

    private int uniqueId;
    private UUID recipeId;

    /**
     * Create a new altar recipe
     *
     * @param altarLevel        The altar level required
     * @param inputs            Input items (max 9 slots for 3x3 grid + focus)
     * @param output            Output item
     * @param constellation     Optional constellation required (can be null)
     * @param starlightRequired Starlight required for crafting
     * @param craftingTime      Time in ticks to craft
     */
    public ASAltarRecipe(TileAltar.AltarLevel altarLevel, ItemStack[] inputs, ItemStack output, String constellation,
        int starlightRequired, int craftingTime) {
        this(altarLevel, inputs, output, constellation, starlightRequired, craftingTime, false, 3, 3);
    }

    /**
     * Create a new altar recipe (with shaped support)
     * Phase 4: Extended constructor for shaped recipes
     *
     * @param altarLevel        The altar level required
     * @param inputs            Input items (max 9 slots for 3x3 grid + focus)
     * @param output            Output item
     * @param constellation     Optional constellation required (can be null)
     * @param starlightRequired Starlight required for crafting
     * @param craftingTime      Time in ticks to craft
     * @param shaped            True if recipe is shaped (ordered)
     * @param width             Recipe width (for shaped recipes)
     * @param height            Recipe height (for shaped recipes)
     */
    public ASAltarRecipe(TileAltar.AltarLevel altarLevel, ItemStack[] inputs, ItemStack output, String constellation,
        int starlightRequired, int craftingTime, boolean shaped, int width, int height) {
        this.altarLevel = altarLevel;
        this.inputs = inputs;
        this.output = output;
        this.constellation = constellation;
        this.starlightRequired = starlightRequired;
        this.craftingTime = craftingTime;
        this.shaped = shaped;
        this.width = width;
        this.height = height;
        this.recipeId = UUID.randomUUID();
    }

    /**
     * Check if this recipe matches the given inputs
     * <p>
     * Phase 4: Now supports both shaped (ordered) and shapeless recipes.
     * Shaped recipes require exact slot positions, shapeless recipes don't.
     *
     * @param inputItems The input items to check
     * @return true if the recipe matches
     */
    public boolean matches(ItemStack[] inputItems) {
        if (shaped) {
            return matchesShaped(inputItems);
        } else {
            return matchesShapeless(inputItems);
        }
    }

    /**
     * Check if this recipe matches in shaped mode (ordered)
     * Phase 4: New method for shaped recipe matching
     *
     * @param inputItems The input items to check
     * @return true if the recipe matches exactly
     */
    private boolean matchesShaped(ItemStack[] inputItems) {
        if (inputItems.length != inputs.length) {
            return false;
        }

        // Exact slot-by-slot matching
        for (int i = 0; i < inputs.length; i++) {
            ItemStack recipeInput = inputs[i];
            ItemStack input = inputItems[i];

            if (recipeInput == null || recipeInput.stackSize <= 0) {
                // Recipe expects empty slot
                if (input != null && input.stackSize > 0) {
                    return false; // Slot should be empty but isn't
                }
            } else {
                // Recipe expects an item
                if (input == null || input.stackSize <= 0) {
                    return false; // Slot should have item but doesn't
                }

                // Check item match
                if (!matches(recipeInput, input)) {
                    return false;
                }

                // Check stack size
                if (input.stackSize < recipeInput.stackSize) {
                    return false;
                }
            }
        }

        // Check constellation requirement
        if (constellation != null && !constellation.isEmpty()) {
            // TODO: Check if constellation is active
            // For now, skip this check
        }

        return true;
    }

    /**
     * Check if this recipe matches in shapeless mode (unordered)
     * <p>
     * Original matching logic preserved
     *
     * @param inputItems The input items to check
     * @return true if the recipe matches
     */
    private boolean matchesShapeless(ItemStack[] inputItems) {
        if (inputItems.length != inputs.length) {
            return false;
        }

        // Create copies to avoid modifying originals
        ItemStack[] recipeCopy = new ItemStack[inputs.length];
        ItemStack[] inputCopy = new ItemStack[inputItems.length];

        for (int i = 0; i < inputs.length; i++) {
            recipeCopy[i] = inputs[i] == null ? null : inputs[i].copy();
            inputCopy[i] = inputItems[i] == null ? null : inputItems[i].copy();
        }

        // Check each recipe input against available inputs
        for (ItemStack recipeInput : recipeCopy) {
            if (recipeInput == null) continue;

            boolean found = false;
            for (int i = 0; i < inputCopy.length; i++) {
                ItemStack input = inputCopy[i];
                if (input == null) continue;

                if (matches(recipeInput, input)) {
                    // Consume the input
                    int newStackSize = input.stackSize - recipeInput.stackSize;
                    if (newStackSize <= 0) {
                        inputCopy[i] = null;
                    } else {
                        input.stackSize = newStackSize;
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        // Check constellation requirement
        if (constellation != null && !constellation.isEmpty()) {
            // TODO: Check if constellation is active
            // For now, skip this check
        }

        return true;
    }

    /**
     * Check if two ItemStacks match
     * Supports both direct item matching and OreDict matching
     */
    private boolean matches(ItemStack recipeInput, ItemStack availableInput) {
        if (availableInput == null || availableInput.stackSize <= 0) {
            return false;
        }

        // Try OreDict match first
        int[] recipeOreIds = OreDictionary.getOreIDs(recipeInput);
        int[] availableOreIds = OreDictionary.getOreIDs(availableInput);
        if (recipeOreIds != null && recipeOreIds.length > 0 && availableOreIds != null && availableOreIds.length > 0) {
            // Check if they share any ore ID
            for (int recipeOreId : recipeOreIds) {
                for (int availableOreId : availableOreIds) {
                    if (recipeOreId == availableOreId) {
                        return availableInput.stackSize >= recipeInput.stackSize;
                    }
                }
            }
        }

        // Direct item match
        if (recipeInput.getItem() == availableInput.getItem()
            && recipeInput.getItemDamage() == availableInput.getItemDamage()
            && (!recipeInput.getHasSubtypes() || recipeInput.getItem() == availableInput.getItem())) {
            return availableInput.stackSize >= recipeInput.stackSize;
        }

        return false;
    }

    /**
     * Check if the given itemstack is the output of this recipe
     * <p>
     * Supports wildcard metadata (32767) for NEI queries.
     * If the item has no subtypes, metadata is ignored.
     */
    public boolean isOutput(ItemStack output) {
        if (output == null || this.output == null) {
            return false;
        }

        // Check item match
        if (output.getItem() != this.output.getItem()) {
            return false;
        }

        // Check metadata with wildcard support
        int recipeMeta = this.output.getItemDamage();
        int queryMeta = output.getItemDamage();

        // Debug logging
        if (java.lang.Boolean.getBoolean("astralsorcery.debug.recipe")) {
            System.out.println("[ASAltarRecipe] isOutput check:");
            System.out.println(
                "  Recipe output: " + this.output
                    .getDisplayName() + " (meta=" + recipeMeta + ", hasSubtypes=" + this.output.getHasSubtypes() + ")");
            System.out.println("  Query output: " + output.getDisplayName() + " (meta=" + queryMeta + ")");
        }

        // Wildcard metadata matches anything
        if (recipeMeta == 32767 || queryMeta == 32767) {
            return true;
        }

        // For items without subtypes, ignore metadata
        if (!this.output.getHasSubtypes()) {
            return true;
        }

        // Otherwise, require exact metadata match
        return recipeMeta == queryMeta;
    }

    /**
     * Get the output item (for matching)
     */
    public ItemStack getOutput() {
        return output;
    }

    /**
     * Get all input items
     */
    public ItemStack[] getInputs() {
        return inputs;
    }

    /**
     * Get the altar level required
     */
    public TileAltar.AltarLevel getAltarLevel() {
        return altarLevel;
    }

    /**
     * Get constellation requirement
     */
    public String getConstellation() {
        return constellation;
    }

    /**
     * Get starlight required
     */
    public int getStarlightRequired() {
        return starlightRequired;
    }

    /**
     * Get crafting time in ticks
     */
    public int getCraftingTime() {
        return craftingTime;
    }

    /**
     * Get unique ID
     */
    public int getUniqueId() {
        return uniqueId;
    }

    /**
     * Set unique ID
     */
    public void updateUniqueId(int id) {
        this.uniqueId = id;
    }

    /**
     * Get recipe ID
     */
    public UUID getRecipeId() {
        return recipeId;
    }

    /**
     * Check if recipe is shaped (ordered)
     * Phase 4: New getter
     */
    public boolean isShaped() {
        return shaped;
    }

    /**
     * Get recipe width (for shaped recipes)
     * Phase 4: New getter
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get recipe height (for shaped recipes)
     * Phase 4: New getter
     */
    public int getHeight() {
        return height;
    }

    /**
     * Clone this recipe
     */
    @Override
    public ASAltarRecipe clone() {
        ItemStack[] inputsCopy = new ItemStack[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            inputsCopy[i] = inputs[i] == null ? null : inputs[i].copy();
        }
        ItemStack outputCopy = output == null ? null : output.copy();

        return new ASAltarRecipe(
            altarLevel,
            inputsCopy,
            outputCopy,
            constellation,
            starlightRequired,
            craftingTime,
            shaped,
            width,
            height);
    }
}
