/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileMapDrawingTable - Star mapping table
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileMapDrawingTable - Star mapping table TileEntity (1.7.10)
 */
public class TileMapDrawingTable extends TileEntityTick {

    public TileMapDrawingTable() {
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
