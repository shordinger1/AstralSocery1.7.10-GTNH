/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Shovel - 1-crystal digging tool
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;

/**
 * ItemCrystalShovel - Crystal shovel (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Uses 1 crystal (max capacity 300)</li>
 * <li>Mining level 3 (emerald tier)</li>
 * <li>Effective against earth, sand, gravel, snow, clay</li>
 * <li>ToolCrystalProperties integration</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>getSubItems() → Signature: (Item, CreativeTabs, List)</li>
 * <li>canHarvestBlock() → func_150897_b(Block)</li>
 * <li>getToolClasses() → Not available in 1.7.10 base</li>
 * <li>IBlockState → Block directly</li>
 * <li>NonNullList → List<ItemStack></li>
 * </ul>
 */
public class ItemCrystalShovel extends ItemCrystalToolBase {

    public ItemCrystalShovel() {
        // 1.7.10: Use ItemCrystalToolBase(int crystalCount, ToolMaterial)
        super(1, net.minecraft.item.Item.ToolMaterial.EMERALD);
    }

    /**
     * Add shovel to creative tab
     * 1.7.10: getSubItems with Item, CreativeTabs, List
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Create max celestial crystal shovel (1 crystal)
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial));
            list.add(stack);
        }
    }

    /**
     * Check if can harvest block
     * 1.7.10: func_150897_b = canHarvestBlock()
     * Shovels can harvest snow and snow layers
     */
    @Override
    public boolean func_150897_b(Block block) {
        if (block == Blocks.snow_layer) {
            return true;
        } else {
            return block == Blocks.snow;
        }
    }

}
