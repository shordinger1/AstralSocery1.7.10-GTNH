/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockMachine
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.block.BlockMachine;

/**
 * ItemBlock for BlockMachine
 * <p>
 * Handles metadata-based variants (Telescope, Grindstone)
 */
public class ItemBlockMachine extends ItemBlock {

    public ItemBlockMachine(Block block) {
        super(block);
        setHasSubtypes(true); // Has metadata variants
    }

    public String getUnlocalizedName(ItemStack stack) {
        // Return different unlocalized names based on metadata
        // Language files use: tile.blockmachine.telescope.name
        int meta = stack.getItemDamage();
        switch (meta) {
            case BlockMachine.META_TELESCOPE:
                return "tile.blockmachine.telescope";
            case BlockMachine.META_GRINDSTONE:
                return "tile.blockmachine.grindstone";
            default:
                return "tile.blockmachine";
        }
    }
}
