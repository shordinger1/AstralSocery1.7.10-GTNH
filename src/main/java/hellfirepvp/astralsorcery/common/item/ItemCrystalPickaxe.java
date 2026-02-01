/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Pickaxe - 3-crystal mining tool
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;

/**
 * ItemCrystalPickaxe - Crystal pickaxe (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Uses 3 crystals (max capacity 900)</li>
 * <li>Mining level 3 (can mine obsidian)</li>
 * <li>5 attack damage</li>
 * <li>ToolCrystalProperties integration</li>
 * <li>Effective against stone, ores, obsidian</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>getSubItems() → Signature: (Item, CreativeTabs, List)</li>
 * <li>canHarvestBlock() → func_150897_b(Block)</li>
 * <li>getToolClasses() → Not available in 1.7.10 base</li>
 * <li>IBlockState → Block directly</li>
 * <li>setHarvestLevel() → Available but different usage</li>
 * <li>NonNullList → List<ItemStack></li>
 * </ul>
 */
public class ItemCrystalPickaxe extends ItemCrystalToolBase {

    public ItemCrystalPickaxe() {
        // 1.7.10: Use ItemCrystalToolBase(int crystalCount, ToolMaterial)
        // Pass 5F damage for pickaxe
        super(3, net.minecraft.item.Item.ToolMaterial.EMERALD);
    }

    /**
     * Add pickaxe to creative tab
     * 1.7.10: getSubItems with Item, CreativeTabs, List
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Create max celestial crystal pickaxe (3 crystals)
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial, maxCelestial));
            list.add(stack);
        }
    }

    /**
     * Check if can harvest block
     * 1.7.10: func_150897_b = canHarvestBlock()
     * Copy-paste from vanilla pickaxe logic
     */
    @Override
    public boolean func_150897_b(Block block) {
        if (block == Blocks.obsidian) {
            return this.toolMaterial.getHarvestLevel() == 3;
        } else if (block != Blocks.diamond_block && block != Blocks.diamond_ore) {
            if (block != Blocks.emerald_ore && block != Blocks.emerald_block) {
                if (block != Blocks.gold_block && block != Blocks.gold_ore) {
                    if (block != Blocks.iron_block && block != Blocks.iron_ore) {
                        if (block != Blocks.lapis_block && block != Blocks.lapis_ore) {
                            if (block != Blocks.redstone_ore && block != Blocks.lit_redstone_ore) {
                                Material material = block.getMaterial();
                                if (material == Material.rock) {
                                    return true;
                                } else if (material == Material.iron) {
                                    return true;
                                } else {
                                    return material == Material.anvil;
                                }
                            } else {
                                return this.toolMaterial.getHarvestLevel() >= 2;
                            }
                        } else {
                            return this.toolMaterial.getHarvestLevel() >= 1;
                        }
                    } else {
                        return this.toolMaterial.getHarvestLevel() >= 1;
                    }
                } else {
                    return this.toolMaterial.getHarvestLevel() >= 2;
                }
            } else {
                return this.toolMaterial.getHarvestLevel() >= 2;
            }
        } else {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
    }

}
