/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTranslucent
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:45
 */
public class TileTranslucent extends TileEntitySynchronized {

    private Block fakedState = Blocks.air;

    public Block getFakedState() {
        return fakedState;
    }

    public void setFakedState(Block fakedState) {
        this.fakedState = fakedState;
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("Block") && compound.hasKey("Data")) {
            // 1.7.10: Data field exists but metadata is handled differently
            Block b = Block.getBlockFromName(compound.getString("Block"));
            if (b != null) {
                fakedState = b;
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (fakedState != null) {
            // 1.7.10: Use block registry to get block identifier
            Object name = Block.blockRegistry.getNameForObject(fakedState);
            compound.setString("Block", name != null ? name.toString() : "");
            compound.setInteger("Data", 0); // 1.7.10: Simplified, metadata is handled differently
        }
    }

}
