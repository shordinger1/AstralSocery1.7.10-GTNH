/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Rose Branch Bow - Bow made from infused wood
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import net.minecraft.item.ItemBow;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Rose Branch Bow
 * <p>
 * A bow crafted from infused rose branches.
 * <p>
 * Features:
 * - Uses infused wood as material
 * - Custom OBJ model (TODO)
 * - Standard bow functionality
 * <p>
 * TODO:
 * - Implement OBJ model rendering
 * - Implement custom arrow behavior
 * - Implement enchantment bonuses
 */
public class ItemRoseBranchBow extends ItemBow {

    public ItemRoseBranchBow() {
        super();
        setMaxDamage(384); // Between wood (59) and bow (384)
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // 1.7.10: Bow properties
        // No sub-items, no metadata variants
    }

    // NOTE: Original version had OBJ model support
    // In 1.7.10, we use standard item rendering
    // TODO: Implement custom model renderer if needed
}
