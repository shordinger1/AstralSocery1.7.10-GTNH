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

import java.util.Random;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.FluidHelper;

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
 * <li>Recipe callbacks for custom effects</li>
 * <li>Support for gated recipes (progression-based)</li>
 * </ul>
 */
public class ASAltarRecipe implements IGatedRecipe {

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
     * Phase 5: Added fluid container support
     */
    private boolean matches(ItemStack recipeInput, ItemStack availableInput) {
        if (availableInput == null || availableInput.stackSize <= 0) {
            return false;
        }

        // Phase 5: Check if recipe input is a fluid container
        // If recipe input is a fluid container, check for any container with same fluid
        if (FluidHelper.hasFluid(recipeInput)) {
            FluidStack recipeFluid = FluidHelper.getFluid(recipeInput);
            if (recipeFluid != null && FluidHelper.hasFluid(availableInput)) {
                FluidStack availableFluid = FluidHelper.getFluid(availableInput);
                // Check if fluids match
                if (availableFluid != null && availableFluid.getFluid() == recipeFluid.getFluid()
                    && availableFluid.amount >= recipeFluid.amount) {
                    return true;
                }
            }
            // Fluid containers must match fluid type
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
     * Check if this recipe slot requires special container item handling
     * Container items are items that should not be consumed (like buckets)
     * Phase 5: Added fluid container support
     *
     * @param slotInput   The input stack in the recipe slot
     * @param actualStack The actual stack in the altar
     * @return true if special consumption is required (container item)
     */
    public boolean requiresSpecialConsumption(ItemStack slotInput, ItemStack actualStack) {
        if (slotInput == null || slotInput.stackSize <= 0) {
            return false;
        }
        if (actualStack == null || actualStack.stackSize <= 0) {
            return false;
        }

        // Phase 5: Check if it's a fluid container
        if (FluidHelper.hasFluid(actualStack)) {
            return true; // Fluid containers need special handling
        }

        // Check if the item has a container item (like buckets)
        ItemStack container = actualStack.getItem()
            .getContainerItem(actualStack);
        if (container != null && container.stackSize > 0) {
            return true;
        }

        return false;
    }

    /**
     * Check if the recipe slot requires fluid consumption
     * Phase 5: New method for fluid handling
     *
     * @param slotInput   The input stack in the recipe slot
     * @param actualStack The actual stack in the altar
     * @return true if this is a fluid container that should be drained
     */
    public boolean requiresFluidConsumption(ItemStack slotInput, ItemStack actualStack) {
        if (slotInput == null || slotInput.stackSize <= 0) {
            return false;
        }
        if (actualStack == null || actualStack.stackSize <= 0) {
            return false;
        }

        // Check if either recipe input or actual stack is a fluid container
        return FluidHelper.hasFluid(slotInput) || FluidHelper.hasFluid(actualStack);
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

    // ========================================================================
    // Recipe Callback System
    // ========================================================================

    /**
     * Called on server side every tick while crafting
     * Override this to add custom server-side crafting effects
     *
     * @param altar             The altar
     * @param state             Current crafting state
     * @param currentTick       Current crafting tick
     * @param totalCraftingTime Total crafting time in ticks
     * @param rand              Random instance
     */
    public void onCraftServerTick(TileAltar altar, ActiveCraftingTask.CraftingState state, int currentTick,
        int totalCraftingTime, Random rand) {
        // Default: do nothing
        // Override in subclasses for custom behavior
    }

    /**
     * Called on server side when crafting completes
     * Override this to add custom completion effects or modify output
     *
     * @param altar The altar
     * @param rand  Random instance
     */
    public void onCraftServerFinish(TileAltar altar, Random rand) {
        // Default: do nothing
        // Override in subclasses for custom behavior
    }

    /**
     * Called on client side every tick while crafting
     * Override this to add custom client-side rendering effects
     *
     * @param altar       The altar
     * @param state       Current crafting state
     * @param currentTick Current crafting tick
     * @param rand        Random instance
     */
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, int currentTick,
        Random rand) {
        // Default: do nothing
        // Override in subclasses for custom rendering effects
    }

    /**
     * Called to apply server-side modifications to the output item
     * Override this to modify the output based on random factors or altar state
     *
     * @param altar The altar
     * @param rand  Random instance
     */
    public void applyOutputModificationsServer(TileAltar altar, Random rand) {
        // Default: do nothing
        // Override in subclasses to modify output item
    }

    /**
     * Check if this recipe allows for chaining
     * If true, the altar will try to start another crafting immediately after completion
     *
     * @return true if chaining is allowed
     */
    public boolean allowsForChaining() {
        return true; // Default: allow chaining
    }

    /**
     * Get the experience rewarded for this recipe
     *
     * @return Experience points
     */
    public int getCraftExperience() {
        return 0; // Default: no experience
    }

    /**
     * Get the experience multiplier for this recipe
     * Based on altar level and other factors
     *
     * @return Experience multiplier (1.0 = normal)
     */
    public float getCraftExperienceMultiplier() {
        return 1.0F; // Default: normal multiplier
    }

    // ========================================================================
    // IGatedRecipe Implementation
    // ========================================================================

    /**
     * Check if the player has the required progression on the server side
     * <p>
     * Default implementation: always returns true (not gated)
     * Override this to add custom progression checks
     *
     * @param player The player to check
     * @return true if player can craft this recipe
     */
    @Override
    public boolean hasProgressionServer(net.minecraft.entity.player.EntityPlayer player) {
        if (player == null) {
            return false;
        }

        // Default: not gated, always accessible
        return true;
    }

    /**
     * Check if the player has the required progression on the client side
     * <p>
     * Default implementation: always returns true (not hidden)
     * Override this to hide recipes until discovered
     *
     * @return true if player can see this recipe
     */
    @Override
    public boolean hasProgressionClient() {
        // Default: always visible
        return true;
    }

    /**
     * Get the required constellation for this recipe
     * <p>
     * Returns the constellation field, which can be set in the constructor
     *
     * @return The constellation name, or null if no constellation required
     */
    @Override
    public String getRequiredConstellation() {
        return constellation;
    }

    /**
     * Get the required research level for this recipe
     * <p>
     * Default implementation: no level required (-1)
     * Override this to add level requirements
     *
     * @return The research level (0 = none), or -1 if no level required
     */
    @Override
    public int getRequiredResearchLevel() {
        return -1; // Default: no level required
    }

    /**
     * Check if this recipe should be completely hidden from the player
     * <p>
     * Default implementation: always show (not hidden)
     * Override this to hide undiscovered recipes
     *
     * @return true to completely hide the recipe
     */
    @Override
    public boolean hideFromRecipeViewer() {
        return false; // Default: always show
    }
}
