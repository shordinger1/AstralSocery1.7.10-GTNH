/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Charged Crystal Shovel - Enhanced digging tool
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.charged;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.ItemCrystalShovel;

/**
 * Charged Crystal Shovel
 * <p>
 * An enhanced version of the crystal shovel made with celestial crystals.
 * <p>
 * Features:
 * - Uses celestial crystal (max size 900)
 * - Higher base efficiency
 * - Faster digging speed
 * - Special constellation effects (TODO)
 * <p>
 * Compared to regular Crystal Shovel:
 * - Larger crystal capacity
 * - Better efficiency multiplier
 * - 1.5x base speed multiplier
 * <p>
 * TODO:
 * - Implement constellation-specific digging effects
 * - Implement glow effect
 * - Link with ItemCelestialCrystal properties
 */
public class ItemChargedCrystalShovel extends ItemCrystalShovel {

    public ItemChargedCrystalShovel() {
        super(); // Calls parent constructor
    }

    public float func_150893_a(ItemStack stack, Block block) {
        // 1.7.10: func_150893_a = getStrVsBlock()
        // Charged shovels are more effective against ground materials
        if (block != null && (block.getMaterial() == Material.ground || block.getMaterial() == Material.grass
            || block.getMaterial() == Material.sand)) {
            // Get base speed from parent class and apply 1.5x multiplier
            float baseSpeed = super.func_150893_a(stack, block);
            return baseSpeed * 1.5F;
        }
        return super.func_150893_a(stack, block);
    }
}
