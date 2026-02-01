/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Axe - 3-crystal chopping tool
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;

/**
 * ItemCrystalAxe - Crystal axe (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Uses 3 crystals (max capacity 900)</li>
 * <li>Mining level 3 (emerald tier)</li>
 * <li>Effective against wood blocks</li>
 * <li>ToolCrystalProperties integration</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>getSubItems() → Signature: (Item, CreativeTabs, List)</li>
 * <li>NonNullList → List<ItemStack></li>
 * <li>getToolClasses() → Not available in 1.7.10 base</li>
 * </ul>
 */
public class ItemCrystalAxe extends ItemCrystalToolBase {

    public ItemCrystalAxe() {
        // 1.7.10: Use ItemCrystalToolBase(int crystalCount, ToolMaterial)
        super(3, net.minecraft.item.Item.ToolMaterial.EMERALD);
    }

    /**
     * Add axe to creative tab
     * 1.7.10: getSubItems with Item, CreativeTabs, List
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Create max celestial crystal axe (3 crystals)
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial, maxCelestial));
            list.add(stack);
        }
    }

}
