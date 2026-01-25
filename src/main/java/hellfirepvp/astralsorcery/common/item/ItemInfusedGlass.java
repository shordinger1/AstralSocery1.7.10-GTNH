/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.starmap.ActiveStarMap;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedGlass
 * Created by HellFirePvP
 * Date: 30.04.2017 / 16:54
 */
// 1.7.10: INBTModel removed - ModelResourceLocation doesn't exist in 1.7.10
public class ItemInfusedGlass extends Item {

    public ItemInfusedGlass() {
        setMaxStackSize(1);
        setMaxDamage(10);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    // 1.7.10: isEnchantable doesn't exist as overrideable method
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 15;
    }

    // 1.7.10: canApplyAtEnchantingTable doesn't exist as overrideable method
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != null && enchantment.equals(Enchantment.unbreaking);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (damage < getDamage(stack)) return;
        // 1.7.10: EnchantmentHelper.getEnchantmentLevel takes (int enchantmentId, ItemStack stack)
        int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
        if (lvl > 0) {
            for (int i = 0; i < lvl; i++) {
                if (itemRand.nextFloat() > 0.7) {
                    return;
                }
            }
        }
        super.setDamage(stack, damage);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return getMapEngravingInformations(stack) != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        ActiveStarMap map = getMapEngravingInformations(stack);
        if (map != null) {
            if (GuiScreen.isShiftKeyDown()) {
                for (IConstellation c : map.getConstellations()) {
                    String out = EnumChatFormatting.GRAY + "- "
                        + EnumChatFormatting.BLUE
                        + I18n.format(c.getUnlocalizedName());
                    // 1.7.10: Use capabilities.isCreativeMode instead of isCreative()
                    if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
                        out += EnumChatFormatting.LIGHT_PURPLE + " (Creative) "
                            + (int) (map.getPercentage(c) * 100)
                            + "%";
                    }
                    tooltip.add(out);
                }
            } else {
                tooltip.add(
                    EnumChatFormatting.DARK_GRAY + EnumChatFormatting.ITALIC.toString()
                        + I18n.format("misc.moreInformation"));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        ActiveStarMap map = getMapEngravingInformations(stack);
        if (map != null) {
            return super.getUnlocalizedName(stack) + ".active";
        }
        return super.getUnlocalizedName(stack);
    }

    public static void setMapEngravingInformations(ItemStack infusedGlassStack, ActiveStarMap map) {
        NBTHelper.getPersistentData(infusedGlassStack)
            .setTag("map", map.serialize());
    }

    @Nullable
    public static ActiveStarMap getMapEngravingInformations(ItemStack infusedGlassStack) {
        NBTTagCompound tag = NBTHelper.getPersistentData(infusedGlassStack);
        if (!tag.hasKey("map")) return null;
        return ActiveStarMap.deserialize(tag.getCompoundTag("map"));
    }

    // 1.7.10: INBTModel methods removed - ModelResourceLocation doesn't exist in 1.7.10
    // Model loading is handled differently in 1.7.10

}
