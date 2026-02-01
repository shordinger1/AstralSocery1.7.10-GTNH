/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockCustomSandOre
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * ItemBlock for BlockCustomSandOre
 */
public class ItemBlockCustomSandOre extends ItemBlock {

    public ItemBlockCustomSandOre(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public int getMetadata(int damage) {
        return damage;
    }
}
