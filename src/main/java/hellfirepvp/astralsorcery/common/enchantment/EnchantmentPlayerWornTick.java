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
 */
public abstract class EnchantmentPlayerWornTick extends EnchantmentBase {

    // 1.7.10: Removed Rarity and EntityEquipmentSlot parameters
    public EnchantmentPlayerWornTick(String name, int weightIn, EnumEnchantmentType typeIn) {
        super(name, weightIn, typeIn);
    }

    public void onWornTick(boolean isClient, EntityPlayer base, int level) {}

}
