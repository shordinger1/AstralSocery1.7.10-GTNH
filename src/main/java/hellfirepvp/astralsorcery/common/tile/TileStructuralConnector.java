/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileStructuralConnector - Structural connector
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileStructuralConnector - Structural connector TileEntity (1.7.10)
 */
public class TileStructuralConnector extends TileEntityTick {

    public TileStructuralConnector() {
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
