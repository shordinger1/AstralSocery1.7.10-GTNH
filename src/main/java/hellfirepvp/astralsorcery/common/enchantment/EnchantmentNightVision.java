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
 */
public class EnchantmentNightVision extends EnchantmentPlayerWornTick {

    public EnchantmentNightVision() {
        super(
            "as.nightvision",
            2, // In 1.7.10: rarity is int (0=common, 1=uncommon, 2=rare)
            EnumEnchantmentType.armor_head); // In 1.7.10: armor_head
    }

    @Override
    public void onWornTick(boolean isClient, EntityPlayer base, int level) {
        // In 1.7.10, PotionEffect takes (int potionId, int duration, int amplifier)
        // Night vision potion ID is typically 16 in vanilla
        base.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, level - 1));
    }

    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, level - 1));
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return type.canEnchantItem(stack.getItem());
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

}
