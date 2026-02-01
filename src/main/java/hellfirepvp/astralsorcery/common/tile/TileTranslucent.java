/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileTranslucent - Stores fake block state for rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;

/**
 * TileTranslucent - Translucent TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Stores a "faked" block state for rendering purposes</li>
 * <li>Makes the block appear as a different block</li>
 * <li>Used for decorative blocks that should look like other blocks</li>
 * <li>Syncs the fake state to clients</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>IBlockState → Block + metadata stored separately</li>
 * <li>Block.REGISTRY.getNameForObject() → Block.blockRegistry.getNameForObject()</li>
 * <li>Block.getBlockFromName() → Block.getBlockFromName()</li>
 * <li>getStateFromMeta() → Same method exists</li>
 * <li>getMetaFromState() → Same method exists</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Make a block appear as stone
 * tile.setFakedState(Blocks.stone, 0);
 * </pre>
 */
public class TileTranslucent extends TileEntitySynchronized {

    // Store block and metadata separately in 1.7.10
    private Block fakedBlock = Blocks.air;
    private int fakedMeta = 0;

    /**
     * Get the faked block
     *
     * @return Faked block
     */
    public Block getFakedBlock() {
        return fakedBlock;
    }

    /**
     * Get the faked metadata
     *
     * @return Faked metadata
     */
    public int getFakedMeta() {
        return fakedMeta;
    }

    /**
     * Set the faked state
     * 1.7.10: Store block and metadata separately
     *
     * @param block Faked block
     * @param meta  Faked metadata
     */
    public void setFakedState(Block block, int meta) {
        this.fakedBlock = block;
        this.fakedMeta = meta;
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        // 1.7.10: Read block and metadata separately
        if (compound.hasKey("Block") && compound.hasKey("Data")) {
            String blockName = compound.getString("Block");
            int data = compound.getInteger("Data");
            Block b = Block.getBlockFromName(blockName);
            if (b != null) {
                this.fakedBlock = b;
                this.fakedMeta = data;
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        // 1.7.10: Write block and metadata separately
        if (fakedBlock != null && fakedBlock != Blocks.air) {
            // 1.7.10: Use block registry to get block name
            String blockName = Block.blockRegistry.getNameForObject(fakedBlock);
            if (blockName != null) {
                compound.setString("Block", blockName);
                compound.setInteger("Data", fakedMeta);
            }
        }
    }

}
