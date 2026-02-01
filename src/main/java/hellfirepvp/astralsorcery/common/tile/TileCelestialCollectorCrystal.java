/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileCelestialCollectorCrystal - Celestial collector crystal
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileCelestialCollectorCrystal - Celestial collector crystal TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Advanced starlight collector</li>
 * <li>TODO: Implement celestial collector logic</li>
 * </ul>
 */
public class TileCelestialCollectorCrystal extends TileEntityTick {

    public TileCelestialCollectorCrystal() {
        super();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        // TODO: Save celestial crystal data
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        // TODO: Load celestial crystal data
    }

    @Override
    protected void onFirstTick() {
        // TODO: Initialize celestial collector
    }
}
