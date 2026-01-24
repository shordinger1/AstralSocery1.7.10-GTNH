/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.ArrayList;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.Sets;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 18.09.2016 / 20:34
 */
public class ItemCrystalShovel extends ItemCrystalToolBase {

    private static final Set<Block> EFFECTIVE_SET = Sets.newHashSet(
        Blocks.CLAY,
        Blocks.DIRT,
        Blocks.FARMLAND,
        Blocks.GRASS,
        Blocks.GRAVEL,
        Blocks.MYCELIUM,
        Blocks.SAND,
        Blocks.SNOW,
        Blocks.SNOW_LAYER,
        Blocks.SOUL_SAND,
        Blocks.GRASS_PATH,
        Blocks.CONCRETE_POWDER);

    public ItemCrystalShovel() {
        super(1, EFFECTIVE_SET);
        setDamageVsEntity(3F);
        setAttackSpeed(-1.5F);
        setHarvestLevel("shovel", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> subItems) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial));
            subItems.add(stack);
        }
    }

    @Override
    public boolean canHarvestBlock(Block blockIn) {
        Block block = blockIn;

        if (block == Blocks.SNOW_LAYER) {
            return true;
        } else {
            return block == Blocks.SNOW;
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return Sets.newHashSet("shovel");
    }
}
