/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;

import net.minecraft.block.Block;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockVariants
 * Created by HellFirePvP
 * Date: 31.07.2016 / 09:30
 */
public interface BlockVariants {

    /**
     * Get all valid states for this block
     * In 1.7.10, returns a list of Block instances (one per variant)
     *
     * @return List of Block instances representing valid states
     */
    public List<Block> getValidStates();

    /**
     * Get the state name for a given metadata value
     *
     * @param metadata The block metadata
     * @return The state name
     */
    public String getStateName(int metadata);

    /**
     * Get the block name (class simple name)
     *
     * @return The block's class simple name
     */
    default public String getBlockName() {
        Block block = (Block) this;
        return block.getLocalizedName();
    }

}
