/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileStructController - Portal structure controller
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileStructController - Portal structure controller TileEntity (1.7.10)
 */
public class TileStructController extends TileEntityTick {

    public TileStructController() {
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
