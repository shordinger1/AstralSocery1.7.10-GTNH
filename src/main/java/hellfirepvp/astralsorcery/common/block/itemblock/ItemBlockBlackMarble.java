/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockBlackMarble
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * ItemBlock for BlockBlackMarble (multi-variant block)
 */
public class ItemBlockBlackMarble extends ItemBlock {

    public ItemBlockBlackMarble(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public int getMetadata(int damage) {
        return damage;

    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return this.field_150939_a.getIcon(2, damage);
    }
}
