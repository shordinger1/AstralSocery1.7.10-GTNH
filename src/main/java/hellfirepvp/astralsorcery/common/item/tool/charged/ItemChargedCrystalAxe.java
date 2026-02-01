/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Charged Crystal Axe - Enhanced chopping tool
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.charged;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.item.ItemCrystalAxe;

/**
 * Charged Crystal Axe
 * <p>
 * An enhanced version of the crystal axe made with celestial crystals.
 * <p>
 * Features:
 * - Uses celestial crystal (max size 900)
 * - Higher base efficiency
 * - Faster chopping speed
 * - Special constellation effects (TODO)
 * <p>
 * Compared to regular Crystal Axe:
 * - Larger crystal capacity
 * - Better efficiency multiplier
 * - 1.5x base speed multiplier
 * <p>
 * TODO:
 * - Implement constellation-specific wood effects
 * - Implement glow effect
 * - Link with ItemCelestialCrystal properties
 */
public class ItemChargedCrystalAxe extends ItemCrystalAxe {

    public ItemChargedCrystalAxe() {
        super(); // Calls parent constructor
    }

    public float func_150893_a(ItemStack stack, Block block) {
        // 1.7.10: func_150893_a = getStrVsBlock()
        // Charged axes are more effective against wood
        if (block != null && block.getMaterial() == net.minecraft.block.material.Material.wood) {
            // Get base speed from parent class
            float baseSpeed = super.func_150893_a(stack, block);
            // Apply 1.5x multiplier for charged tools
            return baseSpeed * 1.5F;
        }
        return super.func_150893_a(stack, block);
    }
}
