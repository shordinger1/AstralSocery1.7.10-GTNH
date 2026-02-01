/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileRitualLink - Ritual link block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileRitualLink - Ritual link TileEntity (1.7.10)
 */
public class TileRitualLink extends TileEntityTick {

    public TileRitualLink() {
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
