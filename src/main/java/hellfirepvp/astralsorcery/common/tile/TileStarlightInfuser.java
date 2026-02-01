/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileStarlightInfuser - Starlight infuser for crystal infusion
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.reference.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileStarlightInfuser - Starlight infuser TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Infuses crystals with starlight</li>
 * <li>Requires multiblock structure to function</li>
 * <li>Consumes starlight during operation</li>
 * <li>Drops infused crystal on completion</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <pre>
 * // Build multiblock structure
 * // Place crystal to infuse in slot
 * // Wait for infusion to complete
 * // Crystal is automatically dropped
 * </pre>
 */
public class TileStarlightInfuser extends TileEntityTick {

    // ========== Recipe System (Simplified) ==========

    /**
     * Simple recipe mapping: Input Item -> Output Item
     * TODO: Expand with more recipes
     */
    private static final Map<Item, ItemStack> RECIPES = new HashMap<Item, ItemStack>();

    static {
        // Rock Crystal -> Celestial Crystal (simplified)
        // Using placeholder items for now
        // RECIPES.put(ItemsAS.itemRockCrystal, new ItemStack(ItemsAS.itemCelestialCrystal));

        // TODO: Add more infusion recipes
    }

    // ========== Infusion Parameters ==========

    /** Infusion progress (0-100%) */
    private int infusionProgress = 0;

    /** Required starlight for infusion */
    private static final int STARLIGHT_REQUIRED = 500;

    /** Starlight consumed per tick */
    private static final double STARLIGHT_PER_TICK = 1.0;

    /** Ticks required for infusion */
    private static final int INFUSION_TICKS = 500; // 25 seconds

    // ========== Inventory ==========

    /** Single slot for input item */
    private ItemStack inputStack = null;

    // ========== State ==========

    /** Whether multiblock structure is complete */
    private boolean hasMultiblock = false;

    /** Whether structure can see sky */
    private boolean canSeeSky = false;

    // ========== Tick Update ==========

    @Override
    public void updateEntity() {
        super.updateEntity();

        // Server-side only
        if (worldObj.isRemote) {
            return;
        }

        // Update state every 16 ticks
        if ((ticksExisted & 15) == 0) {
            updateSkyState();
            updateMultiblockState();
        }

        // Try infusion
        if (hasMultiblock && inputStack != null && inputStack.stackSize > 0) {
            tryInfusion();
        }
    }

    /**
     * Try to infuse the input item
     */
    private void tryInfusion() {
        // Check if recipe exists
        ItemStack output = getRecipeOutput(inputStack);
        if (output == null) {
            return; // No recipe for this item
        }

        // Infuse if can see sky (more starlight)
        if (canSeeSky) {
            infusionProgress++;
        } else {
            // Slower progress without sky
            if (ticksExisted % 2 == 0) {
                infusionProgress++;
            }
        }

        // Check completion
        if (infusionProgress >= INFUSION_TICKS) {
            finishInfusion(output);
        }

        // Mark dirty periodically
        if (ticksExisted % 20 == 0) {
            markDirty();
            markForUpdate();
        }
    }

    /**
     * Finish infusion and drop output
     */
    private void finishInfusion(ItemStack output) {
        // Consume input
        inputStack.stackSize--;
        if (inputStack.stackSize <= 0) {
            inputStack = null;
        }

        // Drop output item
        dropItem(output.copy());

        // Reset progress
        infusionProgress = 0;

        LogHelper.info("Infusion completed at [%d,%d,%d]", xCoord, yCoord, zCoord);
        markDirty();
        markForUpdate();
    }

    /**
     * Get recipe output for input item
     */
    private ItemStack getRecipeOutput(ItemStack input) {
        if (input == null) {
            return null;
        }

        // Check simple recipe map
        ItemStack output = RECIPES.get(input.getItem());
        if (output != null) {
            return output.copy();
        }

        // TODO: Add more complex recipe matching (NBT, damage, etc.)

        return null;
    }

    /**
     * Update multiblock state
     * TODO: Implement StructureLib integration
     */
    private void updateMultiblockState() {
        // Simplified: just check for basic structure
        // TODO: Use StructureLib to check actual pattern

        boolean found = false; // Default: no structure

        // Simple check: require marble in certain positions
        // This is a placeholder - replace with actual StructureLib check

        if (found != this.hasMultiblock) {
            this.hasMultiblock = found;
            LogHelper.debug("Infuser multiblock state: %s at [%d,%d,%d]",
                found, xCoord, yCoord, zCoord);
            markForUpdate();
        }
    }

    /**
     * Update sky visibility state
     */
    private void updateSkyState() {
        boolean seesSky = canBlockSeeTheSky(xCoord, yCoord, zCoord);
        if (canSeeSky != seesSky) {
            canSeeSky = seesSky;
            markForUpdate();
        }
    }

    /**
     * Check if this block can see the sky
     */
    private boolean canBlockSeeTheSky(int x, int y, int z) {
        // Check straight up to world height
        for (int checkY = y + 1; checkY < worldObj.getHeight(); checkY++) {
            if (!worldObj.isAirBlock(x, checkY, z)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Drop item into world
     */
    private void dropItem(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        // Drop item on top of the block
        net.minecraft.entity.item.EntityItem entityItem = new net.minecraft.entity.item.EntityItem(
            worldObj,
            xCoord + 0.5,
            yCoord + 1.2,
            zCoord + 0.5,
            stack
        );

        entityItem.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem);
    }

    // ========== Public API ==========

    /**
     * Get input stack
     */
    public ItemStack getInputStack() {
        return inputStack;
    }

    /**
     * Set input stack
     */
    public void setInputStack(ItemStack stack) {
        this.inputStack = stack;
        markDirty();
        markForUpdate();
    }

    /**
     * Get infusion progress (0.0 to 1.0)
     */
    public float getInfusionProgress() {
        return (float) infusionProgress / (float) INFUSION_TICKS;
    }

    /**
     * Get infusion progress percentage (0-100)
     */
    public int getInfusionProgressPercent() {
        return (infusionProgress * 100) / INFUSION_TICKS;
    }

    /**
     * Check if has multiblock structure
     */
    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    /**
     * Check if can see sky
     */
    public boolean doesSeeSky() {
        return canSeeSky;
    }

    /**
     * Check if currently infusing
     */
    public boolean isInfusing() {
        return hasMultiblock && inputStack != null && infusionProgress > 0;
    }

    /**
     * Add a recipe
     * Public API for adding recipes at runtime
     */
    public static void addRecipe(Item input, ItemStack output) {
        RECIPES.put(input, output);
        LogHelper.info("Added infusion recipe: %s -> %s",
            input.getUnlocalizedName(), output.getUnlocalizedName());
    }

    // ========== NBT ==========

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        // Save infusion data
        compound.setInteger("progress", infusionProgress);
        compound.setBoolean("hasMultiblock", hasMultiblock);
        compound.setBoolean("canSeeSky", canSeeSky);

        // Save inventory
        if (inputStack != null) {
            NBTTagCompound stackCompound = new NBTTagCompound();
            inputStack.writeToNBT(stackCompound);
            compound.setTag("inputStack", stackCompound);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        // Load infusion data
        infusionProgress = compound.getInteger("progress");
        hasMultiblock = compound.getBoolean("hasMultiblock");
        canSeeSky = compound.getBoolean("canSeeSky");

        // Load inventory
        if (compound.hasKey("inputStack")) {
            inputStack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("inputStack"));
        } else {
            inputStack = null;
        }
    }

    // ========== Initialization ==========

    @Override
    protected void onFirstTick() {
        updateSkyState();
        updateMultiblockState();
        LogHelper.debug("Infuser initialized at [%d,%d,%d], multiblock: %s, seesSky: %s",
            xCoord, yCoord, zCoord, hasMultiblock, canSeeSky);
    }
}
