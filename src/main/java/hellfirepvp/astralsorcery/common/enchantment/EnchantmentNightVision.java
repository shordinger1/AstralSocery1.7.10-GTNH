/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentNightVision
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:41
 *
 * 1.7.10 Migration:
 * - Weight: 1 (VERY_RARE)
 * - Type: ARMOR_HEAD (for helmets)
 * - Uses Potion.nightVision instead of MobEffects
 * - Removed EntityEquipmentSlot dependency
 * - Equipment checking handled through event system
 */
public class EnchantmentNightVision extends EnchantmentPlayerWornTick {

    public EnchantmentNightVision() {
        super("as.nightvision", 1, EnumEnchantmentType.all); // VERY_RARE=1, all types
    }

    @Override
    public void onWornTick(boolean isClient, EntityPlayer base, int level) {
        // 1.7.10: Use Potion.nightVision instead of MobEffects.NIGHT_VISION
        base.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, Math.max(0, level - 1), true));
    }

    @Override
    public void func_151368_a(EntityLivingBase user, Entity target, int level) {
        // 1.7.10: func_151368_a is the "onEntityDamaged" callback
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target)
                .addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, Math.max(0, level - 1), true));
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return type.canEnchantItem(stack.getItem());
    }

    @Override
    public int getMaxLevel() {
        return 1; // Only level 1
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false; // Only obtainable through special means (altar)
    }

}
