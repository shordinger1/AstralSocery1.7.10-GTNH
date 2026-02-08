/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.enchantment.EnchantmentNightVision;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentScorchingHeat;
import hellfirepvp.astralsorcery.common.registry.RegistryEnchantments;

/**
 * Enchantment library for Astral Sorcery
 * <p>
 * This class provides centralized access to all Astral Sorcery enchantments.
 * The actual enchantment registration and initialization is handled by
 * {@link hellfirepvp.astralsorcery.common.registry.RegistryEnchantments}.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * This is a convenience wrapper around RegistryEnchantments to maintain
 * API compatibility with the 1.12.2 version.
 *
 * @author HellFirePvP
 */
public class EnchantmentsAS {

    /**
     * Night Vision enchantment
     * <p>
     * Grants the player night vision when worn or applied to appropriate equipment.
     */
    public static EnchantmentNightVision nightVision;

    /**
     * Scorching Heat enchantment
     * <p>
     * Provides fire-related effects when applied to tools or weapons.
     */
    public static EnchantmentScorchingHeat scorchingHeat;

    /**
     * Initialize enchantment references from RegistryEnchantments
     * <p>
     * This method should be called after RegistryEnchantments.preInit() to
     * populate the static fields in this class with the registered enchantment
     * instances.
     */
    public static void init() {
        nightVision = RegistryEnchantments.ENCHANTMENT_NIGHT_VISION;
        scorchingHeat = RegistryEnchantments.ENCHANTMENT_SCORCHING_HEAT;
    }
}
