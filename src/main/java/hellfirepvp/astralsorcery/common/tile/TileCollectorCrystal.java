/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileCollectorCrystal - Starlight collector crystal
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileCollectorCrystal - Collector crystal TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Collects starlight from sky during night</li>
 * <li>Stores starlight internally</li>
 * <li>Provides starlight to connected devices</li>
 * </ul>
 * <p>
 * <b>Collection Mechanics:</b>
 * - Only works at night (13000-23000 world time)
 * - Only works if can see sky (no blocks above)
 * - Collection rate: 1 starlight per tick (configurable)
 * - Max capacity: 1000 starlight
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * // Get stored starlight
 * double starlight = crystal.getStoredStarlight();
 *
 * // Consume starlight (e.g., for altar crafting)
 * double consumed = crystal.consumeStarlight(100);
 * </pre>
 */
public class TileCollectorCrystal extends TileEntityTick {

    // ========== Starlight Storage ==========

    /** Current stored starlight */
    private double storedStarlight = 0;

    /** Maximum starlight capacity */
    private double maxStarlight = 1000;

    /** Collection rate per tick (at night) */
    private double collectionRate = 1.0;

    /** Whether this crystal can see the sky (cached) */
    private boolean canSeeSky = false;

    /** Whether this crystal is currently collecting (cached for display) */
    private boolean isCollecting = false;

    // ========== Tick Update ==========

    @Override
    public void updateEntity() {
        super.updateEntity();

        // Server-side only
        if (worldObj.isRemote) {
            return;
        }

        // Update sky visibility every 100 ticks (5 seconds)
        if (ticksExisted % 100 == 0) {
            updateSkyVisibility();
        }

        // Check collection conditions
        boolean wasCollecting = this.isCollecting;
        this.isCollecting = canCollect();

        // Notify client if collecting state changed
        if (wasCollecting != this.isCollecting) {
            markForUpdate();
            LogHelper.debug("CollectorCrystal at [%d,%d,%d] collecting: %s", xCoord, yCoord, zCoord, this.isCollecting);
        }

        // Collect starlight if conditions met
        if (this.isCollecting) {
            collectStarlight();
        }
    }

    /**
     * Check if this crystal can currently collect starlight
     *
     * @return true if can collect
     */
    private boolean canCollect() {
        // Must be able to see sky
        if (!canSeeSky) {
            return false;
        }

        // Must be night
        if (!isNight()) {
            return false;
        }

        // Must not be full
        if (storedStarlight >= maxStarlight) {
            return false;
        }

        return true;
    }

    /**
     * Collect starlight from the sky
     */
    private void collectStarlight() {
        double collected = Math.min(collectionRate, maxStarlight - storedStarlight);
        storedStarlight += collected;

        // Mark dirty periodically (every 20 ticks = 1 second)
        if (ticksExisted % 20 == 0) {
            markDirty();
        }
    }

    /**
     * Update cached sky visibility
     */
    private void updateSkyVisibility() {
        boolean oldCanSeeSky = this.canSeeSky;
        this.canSeeSky = worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord);

        if (oldCanSeeSky != this.canSeeSky) {
            markForUpdate();
        }
    }

    /**
     * Check if it's currently night
     *
     * @return true if night (13000-23000 world time)
     */
    private boolean isNight() {
        long time = worldObj.getWorldTime() % 24000;
        return time >= 13000 && time <= 23000;
    }

    // ========== Public API ==========

    /**
     * Get current stored starlight
     *
     * @return Stored starlight amount
     */
    public double getStoredStarlight() {
        return storedStarlight;
    }

    /**
     * Get maximum starlight capacity
     *
     * @return Max capacity
     */
    public double getMaxStarlight() {
        return maxStarlight;
    }

    /**
     * Get starlight percentage (0-1)
     *
     * @return Percentage stored
     */
    public double getStarlightPercentage() {
        return maxStarlight > 0 ? storedStarlight / maxStarlight : 0;
    }

    /**
     * Check if crystal can see sky
     *
     * @return true if can see sky
     */
    public boolean canSeeSky() {
        return canSeeSky;
    }

    /**
     * Check if crystal is currently collecting
     *
     * @return true if collecting
     */
    public boolean isCollecting() {
        return isCollecting;
    }

    /**
     * Consume starlight from this crystal
     * Used by altars, machines, etc.
     *
     * @param amount Amount to consume
     * @return Actual amount consumed
     */
    public double consumeStarlight(double amount) {
        if (amount <= 0) {
            return 0;
        }

        double available = Math.min(storedStarlight, amount);
        storedStarlight -= available;
        markDirty();
        markForUpdate(); // Sync to client
        return available;
    }

    /**
     * Add starlight to this crystal
     * Used by transmission system
     *
     * @param amount Amount to add
     * @return Amount actually added (may be less if full)
     */
    public double addStarlight(double amount) {
        if (amount <= 0) {
            return 0;
        }

        double space = maxStarlight - storedStarlight;
        double added = Math.min(space, amount);
        storedStarlight += added;
        markDirty();
        markForUpdate();
        return added;
    }

    // ========== NBT ==========

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        // Save starlight data
        compound.setDouble("storedStarlight", storedStarlight);
        compound.setDouble("maxStarlight", maxStarlight);
        compound.setDouble("collectionRate", collectionRate);
        compound.setBoolean("canSeeSky", canSeeSky);
        compound.setBoolean("isCollecting", isCollecting);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        // Load starlight data
        if (compound.hasKey("storedStarlight")) {
            storedStarlight = compound.getDouble("storedStarlight");
        }
        if (compound.hasKey("maxStarlight")) {
            maxStarlight = compound.getDouble("maxStarlight");
        }
        if (compound.hasKey("collectionRate")) {
            collectionRate = compound.getDouble("collectionRate");
        }
        if (compound.hasKey("canSeeSky")) {
            canSeeSky = compound.getBoolean("canSeeSky");
        }
        if (compound.hasKey("isCollecting")) {
            isCollecting = compound.getBoolean("isCollecting");
        }
    }

    // ========== Initialization ==========

    @Override
    protected void onFirstTick() {
        // Update sky visibility on first tick
        updateSkyVisibility();
        LogHelper.debug("CollectorCrystal initialized at [%d,%d,%d], canSeeSky: %s", xCoord, yCoord, zCoord, canSeeSky);
    }

    // ========== Configuration (for future enhancement) ==========

    /**
     * Set maximum starlight capacity
     * Used for upgrades or special crystal types
     *
     * @param maxCapacity New max capacity
     */
    public void setMaxStarlight(double maxCapacity) {
        this.maxStarlight = Math.max(0, maxCapacity);
        if (storedStarlight > maxStarlight) {
            storedStarlight = maxStarlight;
        }
        markDirty();
        markForUpdate();
    }

    /**
     * Set collection rate
     * Used for upgrades or special crystal types
     *
     * @param rate New collection rate per tick
     */
    public void setCollectionRate(double rate) {
        this.collectionRate = Math.max(0, rate);
        markDirty();
    }
}
