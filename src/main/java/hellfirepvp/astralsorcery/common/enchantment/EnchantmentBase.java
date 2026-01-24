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

    protected EnchantmentBase(String unlocName, Rarity rarityIn, EnumEnchantmentType typeIn,
        net.minecraft.inventory.EntityEquipmentSlot... slots) {
        // 1.7.10: Enchantment constructor takes (int id, Rarity, EnumEnchantmentType)
        // We'll use a dummy ID since proper registration is handled separately
        super(0, rarityIn, typeIn);
        setName(unlocName);
    }

}
