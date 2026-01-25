/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentBase
 * Created by HellFirePvP
 * Date: 05.05.2017 / 15:05
 */
public class EnchantmentBase extends Enchantment {

    // 1.7.10: Removed Rarity parameter - it doesn't exist in 1.7.10
    // Also removed EntityEquipmentSlot... slots parameter - not needed in 1.7.10
    protected EnchantmentBase(String unlocName, int weightIn, EnumEnchantmentType typeIn) {
        // 1.7.10: Enchantment constructor takes (int effectId, int weight, EnumEnchantmentType)
        // We'll use a dummy ID since proper registration is handled separately
        super(0, weightIn, typeIn);
        setName(unlocName);
    }

}
