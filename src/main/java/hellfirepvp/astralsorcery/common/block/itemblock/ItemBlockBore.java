/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockBore
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * ItemBlock for BlockBore (2 variants)
 */
public class ItemBlockBore extends ItemBlock {

    public ItemBlockBore(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public int getMetadata(int damage) {
        return damage;
    }
}
