/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentPlayerWornTick
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:41
 *
 * 1.7.10 Migration:
 * - Simplified constructor (no EntityEquipmentSlot)
 * - onWornTick callback for equipment-based effects
 * - Actual tick logic handled through event system
 *
 * Note: 1.7.10 doesn't have EntityEquipmentSlot, so equipment detection
 * is handled through Baubles API or manual inventory checking in event handlers.
 */
public abstract class EnchantmentPlayerWornTick extends EnchantmentBase {

    public EnchantmentPlayerWornTick(String name, int weight, EnumEnchantmentType typeIn) {
        super(name, weight, typeIn);
    }

    /**
     * Called when this enchantment is on equipped armor and ticks
     * 
     * @param isClient Whether this is the client side
     * @param base     The player wearing the enchanted item
     * @param level    The enchantment level
     */
    public void onWornTick(boolean isClient, EntityPlayer base, int level) {}

}
