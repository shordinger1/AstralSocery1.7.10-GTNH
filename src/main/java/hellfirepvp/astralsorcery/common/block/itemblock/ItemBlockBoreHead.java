/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockBoreHead
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * ItemBlock for BlockBoreHead (multi-variant block)
 */
public class ItemBlockBoreHead extends ItemBlock {

    public ItemBlockBoreHead(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public int getMetadata(int damage) {
        return damage;
    }
}
