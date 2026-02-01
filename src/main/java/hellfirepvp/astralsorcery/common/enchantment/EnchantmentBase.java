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
 *
 * 1.7.10 Migration:
 * - Removed Rarity enum (1.7.10 uses weight int)
 * - Removed EntityEquipmentSlot (1.7.10 doesn't have slot system)
 * - Simplified constructor for 1.7.10 API
 *
 * Rarity to Weight mapping:
 * - VERY_RARE = 1
 * - RARE = 2
 * - UNCOMMON = 5
 * - COMMON = 10
 */
public class EnchantmentBase extends Enchantment {

    /**
     * Constructor for enchantments
     * 
     * @param unlocName Unlocalized name (without "enchantment." prefix)
     * @param weight    Rarity weight (1=rare, 10=common)
     * @param typeIn    Enchantment type
     */
    protected EnchantmentBase(String unlocName, int weight, EnumEnchantmentType typeIn) {
        super(getNextEnchantmentId(), weight, typeIn);
        setName(unlocName);
    }

    /**
     * Auto-incrementing enchantment ID system
     * Starts at 70 to avoid vanilla IDs (0-66 used in vanilla)
     */
    private static int nextId = 70;

    public static int getNextEnchantmentId() {
        return nextId++;
    }

}
