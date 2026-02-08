/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockAltar
 * Handles metadata for different altar types
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * ItemBlock for BlockAltar
 * <p>
 * Handles metadata for different altar types (DISCOVERY, ATTUNEMENT, CONSTELLATION_CRAFT, TRAIT_CRAFT, BRILLIANCE)
 */
public class ItemBlockAltar extends ItemBlock {

    public ItemBlockAltar(Block block) {
        super(block);
        this.setHasSubtypes(true); // Enable metadata variants
        this.setMaxDamage(0); // Not damageable
    }

    @Override
    public int getMetadata(int damage) {
        return damage; // Pass through metadata value
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        // Get unlocalized name based on metadata
        int meta = stack.getItemDamage();
        if (meta < 0 || meta >= 5) {
            meta = 0;
        }
        // Language files use altar_1, altar_2, etc. (1-based indexing)
        return super.getUnlocalizedName() + ".altar_" + (meta + 1);
    }
}
