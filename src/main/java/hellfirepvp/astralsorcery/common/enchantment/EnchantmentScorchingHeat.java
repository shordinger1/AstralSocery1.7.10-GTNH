/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentScorchingHeat
 * Created by HellFirePvP
 * Date: 05.05.2017 / 15:04
 *
 * 1.7.10 Migration:
 * - Weight: 1 (VERY_RARE)
 * - Type: DIGGER (for mining tools)
 * - Auto-smelt functionality handled through block break events
 * - Attack speed components REMOVED (not available in 1.7.10)
 *
 * This enchantment automatically smelts ores and other blocks when mined.
 * Actual implementation is in the BlockBreakEvent handler.
 */
public class EnchantmentScorchingHeat extends EnchantmentBase {

    public EnchantmentScorchingHeat() {
        super("as.smelting", 1, EnumEnchantmentType.digger); // VERY_RARE=1, digger tools
    }

}
