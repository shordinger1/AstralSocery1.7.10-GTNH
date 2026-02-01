/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Charged Crystal Pickaxe - Enhanced mining tool
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.charged;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.ItemCrystalPickaxe;

/**
 * Charged Crystal Pickaxe
 * <p>
 * An enhanced version of the crystal pickaxe made with celestial crystals.
 * <p>
 * Features:
 * - Uses celestial crystal (max size 900)
 * - Higher base efficiency
 * - Special constellation effects (TODO)
 * - Glows when in use (TODO)
 * <p>
 * Compared to regular Crystal Pickaxe:
 * - Larger crystal capacity (900 vs 700)
 * - Better efficiency multiplier
 * - Constellation-specific bonuses
 * <p>
 * TODO:
 * - Implement constellation-specific mining effects
 * - Implement glow effect
 * - Link with ItemCelestialCrystal properties
 * - Implement special particle effects
 */
public class ItemChargedCrystalPickaxe extends ItemCrystalPickaxe {

    public ItemChargedCrystalPickaxe() {
        super(); // Calls parent constructor
    }

    public float func_150893_a(ItemStack stack, Block block) {
        // 1.7.10: func_150893_a = getStrVsBlock()
        // Charged tools have better base efficiency against mining blocks
        if (block != null && (block == Blocks.obsidian || block == Blocks.diamond_ore
            || block == Blocks.gold_ore
            || block == Blocks.iron_ore
            || block == Blocks.lapis_ore
            || block == Blocks.redstone_ore
            || block == Blocks.lit_redstone_ore)) {
            // Get base speed from parent class and apply 1.5x multiplier
            float baseSpeed = super.func_150893_a(stack, block);
            return baseSpeed * 1.5F;
        }
        return super.func_150893_a(stack, block);
    }
}
