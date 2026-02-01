/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileTreeBeacon - Tree beacon
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileTreeBeacon - Tree beacon TileEntity (1.7.10)
 */
public class TileTreeBeacon extends TileEntityTick {

    public TileTreeBeacon() {
        super();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
    }
}
