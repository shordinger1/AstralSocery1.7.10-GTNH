/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileCrystalLens - Crystal lens for starlight transmission
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileCrystalLens - Crystal lens TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Transmits starlight from input side to output side</li>
 * <li>Input side: Opposite of block facing</li>
 * <li>Output side: Block facing direction</li>
 * <li>Efficiency based on distance</li>
 * <li>Maximum transmission range: 16 blocks</li>
 * </ul>
 * <p>
 * <b>Transmission Mechanics:</b>
 * - Lens pulls starlight from input side (collectors, other lenses)
 * - Lens pushes starlight to output side (altars, machines)
 * - Transmission rate: 10 starlight per tick
 * - Efficiency: decreases with distance (1.0 at 0 blocks, 0.5 at 16 blocks)
 * <p>
 * <b>Usage:</b>
 * <pre>
 * // Place lens facing towards altar
 * // Lens will pull from behind (collector)
 * // And push to front (altar)
 * </pre>
 */
public class TileCrystalLens extends TileEntityTick {

    // ========== Transmission Parameters ==========

    /** Maximum transmission range */
    private static final int MAX_RANGE = 16;

    /** Maximum transmission per tick */
    private static final double MAX_TRANSMISSION = 10.0;

    /** Transmission efficiency multiplier */
    private double efficiency = 1.0;

    /** Currently stored starlight (buffer) */
    private double bufferedStarlight = 0;

    /** Maximum buffer capacity */
    private static final double MAX_BUFFER = 100;

    /** Lens facing direction */
    private ForgeDirection facing = ForgeDirection.UP;

    // ========== Tick Update ==========

    @Override
    public void updateEntity() {
        super.updateEntity();

        // Server-side only
        if (worldObj.isRemote) {
            return;
        }

        // Update facing direction from block metadata
        updateFacing();

        // Periodically transmit starlight (every 5 ticks = 0.25 seconds)
        if (ticksExisted % 5 == 0) {
            transmitStarlight();
        }
    }

    /**
     * Update facing direction from block metadata
     */
    private void updateFacing() {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        ForgeDirection newFacing = getDirectionFromMetadata(meta);

        if (newFacing != this.facing) {
            this.facing = newFacing;
            markDirty();
        }
    }

    /**
     * Get ForgeDirection from block metadata (0-5)
     */
    private ForgeDirection getDirectionFromMetadata(int meta) {
        switch (meta) {
            case 0: return ForgeDirection.DOWN;
            case 1: return ForgeDirection.UP;
            case 2: return ForgeDirection.NORTH;
            case 3: return ForgeDirection.SOUTH;
            case 4: return ForgeDirection.WEST;
            case 5: return ForgeDirection.EAST;
            default: return ForgeDirection.UP;
        }
    }

    /**
     * Main transmission logic
     */
    private void transmitStarlight() {
        // First, pull from input side (behind the lens)
        pullFromInputSide();

        // Then, push to output side (in front of the lens)
        pushToOutputSide();
    }

    /**
     * Pull starlight from input side (behind the lens)
     */
    private void pullFromInputSide() {
        ForgeDirection inputSide = facing.getOpposite();

        // Check if buffer is full
        if (bufferedStarlight >= MAX_BUFFER) {
            return;
        }

        // Look for starlight sources on input side
        double space = MAX_BUFFER - bufferedStarlight;
        double pullAmount = Math.min(space, MAX_TRANSMISSION);

        // Search in the direction of input side
        for (int dist = 1; dist <= MAX_RANGE; dist++) {
            int x = xCoord + inputSide.offsetX * dist;
            int y = yCoord + inputSide.offsetY * dist;
            int z = zCoord + inputSide.offsetZ * dist;

            // Check if tile entity is a starlight source
            if (worldObj.blockExists(x, y, z)) {
                net.minecraft.tileentity.TileEntity te = worldObj.getTileEntity(x, y, z);

                if (te instanceof TileCollectorCrystal) {
                    // Pull from collector crystal
                    TileCollectorCrystal collector = (TileCollectorCrystal) te;
                    double pulled = collector.consumeStarlight(pullAmount);
                    bufferedStarlight += pulled;

                    if (pulled > 0) {
                        LogHelper.debug("Lens at [%d,%d,%d] pulled %.1f starlight from collector at [%d,%d,%d]",
                            xCoord, yCoord, zCoord, pulled, x, y, z);
                    }

                    // Only pull from one source per tick
                    break;
                }
            }
        }
    }

    /**
     * Push starlight to output side (in front of the lens)
     */
    private void pushToOutputSide() {
        // Check if buffer is empty
        if (bufferedStarlight <= 0) {
            return;
        }

        // Calculate efficiency based on distance to target
        // Search for targets on output side
        for (int dist = 1; dist <= MAX_RANGE; dist++) {
            int x = xCoord + facing.offsetX * dist;
            int y = yCoord + facing.offsetY * dist;
            int z = zCoord + facing.offsetZ * dist;

            // Check if tile entity is a starlight consumer
            if (worldObj.blockExists(x, y, z)) {
                net.minecraft.tileentity.TileEntity te = worldObj.getTileEntity(x, y, z);

                // Check for altar (has addStarlight method or similar)
                if (isStarlightConsumer(te)) {
                    // Calculate transmission efficiency based on distance
                    double efficiency = calculateTransmissionEfficiency(dist);

                    // Push starlight
                    double pushAmount = Math.min(bufferedStarlight, MAX_TRANSMISSION * efficiency);
                    double pushed = pushStarlightTo(te, pushAmount);

                    bufferedStarlight -= pushed;

                    if (pushed > 0) {
                        LogHelper.debug("Lens at [%d,%d,%d] pushed %.1f starlight to [%d,%d,%d] (efficiency: %.2f)",
                            xCoord, yCoord, zCoord, pushed, x, y, z, efficiency);
                    }

                    // Only push to one target per tick
                    markDirty();
                    return;
                }
            }
        }
    }

    /**
     * Calculate transmission efficiency based on distance
     *
     * @param distance Distance in blocks
     * @return Efficiency (0.0 to 1.0)
     */
    private double calculateTransmissionEfficiency(int distance) {
        // Linear falloff from 1.0 at 0 blocks to 0.5 at MAX_RANGE blocks
        return 1.0 - (distance / (2.0 * MAX_RANGE));
    }

    /**
     * Check if tile entity is a starlight consumer
     */
    private boolean isStarlightConsumer(net.minecraft.tileentity.TileEntity te) {
        // Check if it's an altar (has starlight storage)
        if (te instanceof TileAltar) {
            return true;
        }

        // TODO: Add other consumers (machines, etc.)

        return false;
    }

    /**
     * Push starlight to a consumer
     *
     * @param te Consumer tile entity
     * @param amount Amount to push
     * @return Amount actually pushed
     */
    private double pushStarlightTo(net.minecraft.tileentity.TileEntity te, double amount) {
        if (te instanceof TileAltar) {
            TileAltar altar = (TileAltar) te;

            // Get current starlight
            int current = altar.getStarlightStored();
            int max = altar.getMaxStarlightStorage();
            int space = max - current;

            double pushAmount = Math.min(space, amount);
            if (pushAmount > 0) {
                altar.setStarlightStored(current + (int) pushAmount);
                return pushAmount;
            }
        }

        return 0;
    }

    // ========== Public API ==========

    /**
     * Get buffered starlight
     *
     * @return Buffered amount
     */
    public double getBufferedStarlight() {
        return bufferedStarlight;
    }

    /**
     * Get lens facing direction
     *
     * @return Facing direction
     */
    public ForgeDirection getFacing() {
        return facing;
    }

    /**
     * Get transmission efficiency
     *
     * @return Efficiency (0.0 to 1.0)
     */
    public double getEfficiency() {
        return efficiency;
    }

    /**
     * Set transmission efficiency (used for upgrades/buffs)
     *
     * @param efficiency New efficiency
     */
    public void setEfficiency(double efficiency) {
        this.efficiency = Math.max(0, Math.min(1, efficiency));
        markDirty();
    }

    // ========== NBT ==========

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        // Save transmission data
        compound.setDouble("bufferedStarlight", bufferedStarlight);
        compound.setDouble("efficiency", efficiency);
        compound.setInteger("facingOrdinal", facing.ordinal());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        // Load transmission data
        if (compound.hasKey("bufferedStarlight")) {
            bufferedStarlight = compound.getDouble("bufferedStarlight");
        }
        if (compound.hasKey("efficiency")) {
            efficiency = compound.getDouble("efficiency");
        }
        if (compound.hasKey("facingOrdinal")) {
            int ordinal = compound.getInteger("facingOrdinal");
            if (ordinal >= 0 && ordinal < ForgeDirection.values().length) {
                facing = ForgeDirection.values()[ordinal];
            }
        }
    }

    // ========== Initialization ==========

    @Override
    protected void onFirstTick() {
        updateFacing();
        LogHelper.debug("Lens initialized at [%d,%d,%d], facing: %s",
            xCoord, yCoord, zCoord, facing);
    }
}
