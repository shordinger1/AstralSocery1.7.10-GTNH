/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ActiveCraftingTask - Active crafting task for altar recipes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Active crafting task for altar recipes
 * <p>
 * Tracks the progress of an active crafting operation on an altar.
 * Handles recipe matching, starlight requirements, and crafting completion.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * <ul>
 * <li>Simplified state tracking</li>
 * <li>Basic constellation checking</li>
 * <li>Player owner tracking</li>
 * </ul>
 */
public class ActiveCraftingTask {

    public enum CraftingState {
        IDLE, // Not crafting
        RUNNING, // Actively crafting
        PAUSED, // Waiting for starlight
        COMPLETE // Finished
    }

    private final ASAltarRecipe recipe;
    private final UUID playerUUID;
    private final int craftingTicks;
    private final int craftingDivisor;

    private int currentTick;
    private CraftingState state;
    private UUID taskId;

    /**
     * Create a new active crafting task
     *
     * @param recipe          The recipe being crafted
     * @param craftingDivisor Speed divisor based on altar level
     * @param playerUUID      The player who started crafting
     */
    public ActiveCraftingTask(ASAltarRecipe recipe, int craftingDivisor, UUID playerUUID) {
        this.recipe = recipe;
        this.playerUUID = playerUUID;
        this.craftingDivisor = craftingDivisor;
        this.craftingTicks = recipe.getCraftingTime() / craftingDivisor;
        this.currentTick = 0;
        this.state = CraftingState.RUNNING;
        this.taskId = UUID.randomUUID();
    }

    /**
     * Update the crafting task
     * Called every tick while crafting
     *
     * @param altar The altar
     * @return true if crafting should continue, false if should abort
     */
    public boolean update(TileAltar altar) {
        if (state == CraftingState.COMPLETE) {
            return false; // Already complete
        }

        if (state == CraftingState.PAUSED) {
            // Check if starlight requirements are now met
            if (altar.getStarlightStored() >= recipe.getStarlightRequired()) {
                state = CraftingState.RUNNING;
                LogHelper.debug(
                    "Crafting resumed for recipe: " + recipe.getOutput()
                        .getDisplayName());
            } else {
                return true; // Still paused
            }
        }

        // Check starlight requirement
        if (altar.getStarlightStored() < recipe.getStarlightRequired()) {
            state = CraftingState.PAUSED;
            return true; // Pause but don't abort
        }

        // Increment crafting progress
        currentTick++;

        // Check if complete
        if (currentTick >= craftingTicks) {
            state = CraftingState.COMPLETE;
            LogHelper.info(
                "Crafting complete: " + recipe.getOutput()
                    .getDisplayName());
            return false;
        }

        return true;
    }

    /**
     * Check if the recipe still matches
     *
     * @param altar The altar
     * @return true if recipe matches
     */
    public boolean doesRecipeMatch(TileAltar altar) {
        ItemStack[] inventory = new ItemStack[altar.getInventorySize()];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = altar.getInventory()
                .getStackInSlot(i);
        }

        return recipe.matches(inventory);
    }

    /**
     * Complete the crafting
     *
     * @param altar The altar
     * @return The output item stack
     */
    public ItemStack complete(TileAltar altar) {
        ItemStack output = recipe.getOutput()
            .copy();

        // Consume input items
        for (int i = 0; i < altar.getInventorySize(); i++) {
            ItemStack stack = altar.getInventory()
                .getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                altar.getInventory()
                    .extractItem(i, 1, false);
            }
        }

        // Consume starlight
        int starlightConsumed = recipe.getStarlightRequired();
        if (!altar.consumeStarlight(starlightConsumed)) {
            LogHelper.warn("Failed to consume " + starlightConsumed + " starlight for crafting!");
        }

        LogHelper
            .debug("Recipe completed: " + output.getDisplayName() + " - Consumed " + starlightConsumed + " starlight");

        return output;
    }

    /**
     * Serialize to NBT
     */
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();

        // Serialize recipe
        NBTTagCompound recipeNbt = new NBTTagCompound();
        recipeNbt.setString(
            "recipeId",
            recipe.getRecipeId()
                .toString());
        nbt.setTag("recipe", recipeNbt);

        // Serialize other data
        // 1.7.10: setUniqueId() doesn't exist, use setLong for UUID parts
        nbt.setLong("playerUUID_most", playerUUID.getMostSignificantBits());
        nbt.setLong("playerUUID_least", playerUUID.getLeastSignificantBits());
        nbt.setInteger("craftingTicks", craftingTicks);
        nbt.setInteger("craftingDivisor", craftingDivisor);
        nbt.setInteger("currentTick", currentTick);
        nbt.setInteger("state", state.ordinal());
        nbt.setLong("taskId_most", taskId.getMostSignificantBits());
        nbt.setLong("taskId_least", taskId.getLeastSignificantBits());

        return nbt;
    }

    /**
     * Deserialize from NBT
     *
     * @param nbt   The NBT compound to read from
     * @param altar The altar (for recipe matching)
     * @return The deserialized task, or null if recipe not found
     */
    public static ActiveCraftingTask deserialize(NBTTagCompound nbt, TileAltar altar) {
        try {
            // Read player UUID
            long playerMost = nbt.getLong("playerUUID_most");
            long playerLeast = nbt.getLong("playerUUID_least");
            UUID playerUUID = new UUID(playerMost, playerLeast);

            // Read crafting parameters
            int craftingTicks = nbt.getInteger("craftingTicks");
            int craftingDivisor = nbt.getInteger("craftingDivisor");
            int currentTick = nbt.getInteger("currentTick");
            int stateOrdinal = nbt.getInteger("state");

            // Read task ID
            long taskIdMost = nbt.getLong("taskId_most");
            long taskIdLeast = nbt.getLong("taskId_least");

            // Find matching recipe from altar's current inventory
            // This is necessary because we can't store full recipe in NBT easily
            ASAltarRecipe recipe = findMatchingRecipe(altar);
            if (recipe == null) {
                LogHelper.warn("Could not find matching recipe for deserialized crafting task");
                return null;
            }

            // Create new task with deserialized data
            ActiveCraftingTask task = new ActiveCraftingTask(recipe, craftingDivisor, playerUUID);
            task.currentTick = currentTick;
            task.state = CraftingState.values()[stateOrdinal];
            task.taskId = new UUID(taskIdMost, taskIdLeast);

            LogHelper.debug("Deserialized crafting task: " + task.taskId);
            return task;

        } catch (Exception e) {
            LogHelper.error("Failed to deserialize ActiveCraftingTask: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find a matching recipe for the altar's current inventory
     */
    private static ASAltarRecipe findMatchingRecipe(TileAltar altar) {
        if (altar == null) {
            return null;
        }

        // Get current inventory
        ItemStack[] inventory = new ItemStack[altar.getInventorySize()];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = altar.getInventory()
                .getStackInSlot(i);
        }

        // Try to find matching recipe
        return AltarRecipeRegistry.findRecipe(inventory, altar.getAltarLevel());
    }

    // Getters
    public ASAltarRecipe getRecipe() {
        return recipe;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getCraftingTicks() {
        return craftingTicks;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public float getProgress() {
        return craftingTicks > 0 ? (float) currentTick / (float) craftingTicks : 0.0F;
    }

    public CraftingState getState() {
        return state;
    }

    public UUID getTaskId() {
        return taskId;
    }

    /**
     * Check if this task should persist (not abort)
     * <p>
     * A task should persist if:
     * - The recipe still matches the altar's inventory
     * - The altar still has enough starlight (or is paused)
     * - The task is not already complete
     *
     * @param altar The altar
     * @return true if task should persist, false if should abort
     */
    public boolean shouldPersist(TileAltar altar) {
        if (altar == null) {
            return false;
        }

        // Always persist if complete (let it be handled elsewhere)
        if (state == CraftingState.COMPLETE) {
            return true;
        }

        // Check if recipe still matches
        if (!doesRecipeMatch(altar)) {
            LogHelper.debug("Crafting task aborted: recipe no longer matches");
            return false;
        }

        // Check if starlight requirement is met or we're paused
        int starlightStored = altar.getStarlightStored();
        int starlightRequired = recipe.getStarlightRequired();

        if (starlightStored < starlightRequired * 0.5) {
            // Less than 50% of required starlight
            // Only persist if we're already paused (give it a chance to recover)
            if (state != CraftingState.PAUSED) {
                LogHelper.debug("Crafting task aborted: insufficient starlight");
                return false;
            }
        }

        return true;
    }
}
