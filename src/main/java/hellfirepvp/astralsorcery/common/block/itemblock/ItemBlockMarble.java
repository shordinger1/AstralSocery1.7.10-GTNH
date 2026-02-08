/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockMarble
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.block.BlockMarble;

/**
 * ItemBlock for BlockMarble (multi-variant block)
 * <p>
 * Handles metadata-based variants with proper localization support.
 * Each variant has its own localization key:
 * - tile.blockmarble.raw.name
 * - tile.blockmarble.bricks.name
 * - tile.blockmarble.pillar.name
 * - etc.
 */
public class ItemBlockMarble extends ItemBlock {

    public ItemBlockMarble(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        // Get variant name from metadata
        int meta = stack.getItemDamage();
        BlockMarble.MarbleType type = BlockMarble.MarbleType.byMetadata(meta);
        String variantName = type.getName();

        // Return localization key: tile.blockmarble.{variant}.name
        return this.field_150939_a.getUnlocalizedName() + "." + variantName;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        // For pillar variants (2, 3, 4), return null to force ItemRenderer usage
        // For other variants, use standard icon rendering
        if (damage == 2 || damage == 3 || damage == 4) {
            return null; // Use ItemRenderer for pillar variants
        }
        return this.field_150939_a.getIcon(2, damage);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        int damage = stack.getItemDamage();
        // For pillar variants (2, 3, 4), return null to force ItemRenderer usage
        if (damage == 2 || damage == 3 || damage == 4) {
            return null; // Use ItemRenderer for pillar variants
        }
        return super.getIcon(stack, pass);
    }
}
