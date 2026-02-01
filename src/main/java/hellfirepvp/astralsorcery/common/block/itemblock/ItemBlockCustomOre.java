/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockCustomOre
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * ItemBlock for BlockCustomOre
 */
public class ItemBlockCustomOre extends ItemBlock {

    public ItemBlockCustomOre(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public int getMetadata(int damage) {
        return damage;
    }
}
