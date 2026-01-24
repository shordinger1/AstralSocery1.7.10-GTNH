/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantHelper;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantment;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEnchantmentAmulet
 * Created by HellFirePvP
 * Date: 25.01.2018 / 19:05
 */
public class ItemEnchantmentAmulet extends Item implements ItemDynamicColor, IBauble {

    private static Random rand = new Random();

    public ItemEnchantmentAmulet() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> items) {
        // 1.7.10: Use tab == this.getCreativeTab() instead of isInCreativeTab()
        if (tab == this.getCreativeTab()) {
            ItemStack stack = new ItemStack(this);

            items.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        List<AmuletEnchantment> enchantments = getAmuletEnchantments(stack);
        for (AmuletEnchantment ench : enchantments) {
            tooltip.add(EnumChatFormatting.BLUE + ench.getDescription());
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // 1.7.10: getAmuletColor returns nullable Integer, not Optional
        if (!worldIn.isRemote && getAmuletColor(stack) == null) {
            freezeAmuletColor(stack);
        }
        if (!worldIn.isRemote && getAmuletEnchantments(stack).isEmpty()) {
            AmuletEnchantHelper.rollAmulet(stack);
        }
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;
        // 1.7.10: getAmuletColor returns nullable Integer, not Optional
        Integer color = getAmuletColor(stack);
        if (color != null) {
            return color;
        }
        int tick = (int) (ClientScheduler.getClientTick() % 500000L);
        int c = Color.getHSBColor((tick / 500000F) * 360F, 0.7F, 1F)
            .getRGB();
        return c | 0xFF000000;
    }

    // 1.7.10: Optional doesn't exist, use nullable Integer instead
    @Nullable
    public static Integer getAmuletColor(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return null;
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (!tag.hasKey("amuletColor")) {
            return null;
        }
        return tag.getInteger("amuletColor");
    }

    public static void freezeAmuletColor(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (tag.hasKey("amuletColor")) {
            return;
        }
        if (rand.nextInt(400) == 0) {
            tag.setInteger("amuletColor", 0xFFFFFFFF);
        } else {
            float hue = rand.nextFloat() * 360F;
            tag.setInteger(
                "amuletColor",
                Color.getHSBColor(hue, 0.7F, 1.0F)
                    .getRGB() | 0xFF000000);
        }
    }

    public static List<AmuletEnchantment> getAmuletEnchantments(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Lists.newArrayList();
        }

        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (!tag.hasKey("amuletEnchantments")) {
            return Lists.newArrayList();
        }
        NBTTagList enchants = tag.getTagList("amuletEnchantments", Constants.NBT.TAG_COMPOUND);
        List<AmuletEnchantment> enchantments = new ArrayList<>(enchants.tagCount());
        for (int i = 0; i < enchants.tagCount(); i++) {
            AmuletEnchantment ench = AmuletEnchantment.deserialize(enchants.getCompoundTagAt(i));
            if (ench != null) {
                enchantments.add(ench);
            }
        }
        enchantments.sort(Comparator.comparing((AmuletEnchantment ae) -> ae.getType()));
        return enchantments;
    }

    public static void setAmuletEnchantments(ItemStack stack, List<AmuletEnchantment> enchantments) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        enchantments.sort(Comparator.comparing((AmuletEnchantment ae) -> ae.getType()));

        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        NBTTagList enchants = tag.hasKey("amuletEnchantments", Constants.NBT.TAG_COMPOUND)
            ? tag.getTagList("amuletEnchantments", Constants.NBT.TAG_COMPOUND)
            : new NBTTagList();
        for (AmuletEnchantment enchant : enchantments) {
            enchants.appendTag(enchant.serialize());
        }
        tag.setTag("amuletEnchantments", enchants);
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(null /* TODO: SoundEvents - needs 1.7.10 sound string */, .65F, 6.4f);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(null /* TODO: SoundEvents - needs 1.7.10 sound string */, .65F, 6.4f);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

}
