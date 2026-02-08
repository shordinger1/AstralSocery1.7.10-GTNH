/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Rose Branch Bow - Bow made from infused wood
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import net.minecraft.item.ItemBow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    // ========== Icon Registration (1.7.10) ==========

    /**
     * Register icons for the bow
     * ItemBow subclasses need to explicitly register icons in 1.7.10
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        // Get the texture name that was set by setTextureName() in RegistryItems
        String iconString = this.getIconString();
        if (iconString != null) {
            this.itemIcon = register.registerIcon(iconString);
        } else {
            // Fallback: use unlocalizedName without "item." prefix
            String unlocalized = this.getUnlocalizedName();
            if (unlocalized.startsWith("item.")) {
                unlocalized = unlocalized.substring(5);
            }
            this.itemIcon = register.registerIcon("astralsorcery:" + unlocalized);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.IIcon getIconFromDamage(int damage) {
        return this.itemIcon;
    }

    @Override
    public String getItemStackDisplayName(net.minecraft.item.ItemStack stack) {
        // Try to get localized name from language file
        String localized = net.minecraft.util.StatCollector.translateToLocal("item.itemrosebranchbow.name");
        // If translation not found (returns the key itself), use hardcoded name
        if (localized.equals("item.itemrosebranchbow.name")) {
            return "Vantage";
        }
        return localized;
    }
}
