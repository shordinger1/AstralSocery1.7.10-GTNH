/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 18.09.2016 / 20:34
 */
public class ItemCrystalShovel extends ItemCrystalToolBase {

    // 1.7.10: GRASS_PATH and CONCRETE_POWDER don't exist, removed
    // 1.7.10: Block names are lowercase (e.g., dirt not DIRT)
    private static final Set<Block> EFFECTIVE_SET = new HashSet<Block>();

    static {
        EFFECTIVE_SET.add(Blocks.clay);
        EFFECTIVE_SET.add(Blocks.dirt);
        EFFECTIVE_SET.add(Blocks.farmland);
        EFFECTIVE_SET.add(Blocks.grass);
        EFFECTIVE_SET.add(Blocks.gravel);
        EFFECTIVE_SET.add(Blocks.mycelium);
        EFFECTIVE_SET.add(Blocks.sand);
        EFFECTIVE_SET.add(Blocks.snow);
        EFFECTIVE_SET.add(Blocks.snow_layer);
        EFFECTIVE_SET.add(Blocks.soul_sand);
    }

    public ItemCrystalShovel() {
        super(1, EFFECTIVE_SET);
        setDamageVsEntity(3F);
        setAttackSpeed(-1.5F);
        setHarvestLevel("shovel", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial));
            list.add(stack);
        }
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        // 1.7.10: canHarvestBlock takes ItemStack parameter, block names are lowercase
        if (block == Blocks.snow_layer) {
            return true;
        } else {
            return block == Blocks.snow;
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        Set<String> set = new HashSet<String>();
        set.add("shovel");
        return set;
    }
}
