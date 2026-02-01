/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileWorldIlluminator - World illuminator
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileWorldIlluminator - World illuminator TileEntity (1.7.10)
 */
public class TileWorldIlluminator extends TileEntityTick {

    public TileWorldIlluminator() {
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
