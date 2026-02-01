/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileFakeTree - Fake tree block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileFakeTree - Fake tree TileEntity (1.7.10)
 */
public class TileFakeTree extends TileEntityTick {

    public TileFakeTree() {
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
