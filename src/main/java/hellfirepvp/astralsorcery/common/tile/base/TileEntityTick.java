/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileEntityTick - Base class for TileEntities that tick
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import net.minecraft.nbt.NBTTagCompound;

/**
 * TileEntityTick - Ticking TileEntity base class (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Provides updateEntity() ticking functionality for TileEntities</li>
 * <li>Tracks ticksExisted counter for NBT persistence</li>
 * <li>Calls onFirstTick() for one-time initialization</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>update() → updateEntity()</li>
 * <li>ITickable interface - Doesn't exist in 1.7.10</li>
 * <li>TileEntity already has ticksExisted field, but we track our own for NBT</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 *
 * <pre>
 * public class MyTile extends TileEntityTick {
 *
 *     &#64;Override
 *     protected void onFirstTick() {
 *         // One-time initialization
 *     }
 *
 *     &#64;Override
 *     public void updateEntity() {
 *         super.updateEntity(); // Important: call super.updateEntity()
 *         // Your tick logic here
 *     }
 * }
 * </pre>
 */
public abstract class TileEntityTick extends TileEntitySynchronized {

    // Custom tick counter for NBT persistence
    // Note: TileEntity in 1.7.10 has its own ticksExisted field,
    // but we track our own to ensure proper NBT save/load
    protected int ticksExisted = 0;

    /**
     * Update method called every tick
     * 1.7.10: Override updateEntity() directly (no ITickable interface)
     */
    @Override
    public void updateEntity() {
        // Call first tick on initialization
        if (ticksExisted == 0) {
            onFirstTick();
        }

        ticksExisted++;
    }

    /**
     * Called on the first tick after the TileEntity is created/loaded
     * Use for one-time initialization that requires the world to be loaded
     * Default implementation does nothing - override as needed
     */
    protected void onFirstTick() {
        // Default implementation - override in subclasses if needed
    }

    /**
     * Get the number of ticks this TileEntity has existed
     *
     * @return Number of ticks
     */
    public int getTicksExisted() {
        return ticksExisted;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        ticksExisted = compound.getInteger("ticksExisted");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("ticksExisted", ticksExisted);
    }

}
