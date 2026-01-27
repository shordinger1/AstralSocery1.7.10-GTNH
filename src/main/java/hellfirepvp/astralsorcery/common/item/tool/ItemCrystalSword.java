/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.entities.EntityCrystalTool;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalSword
 * Created by HellFirePvP
 * Date: 19.09.2016 / 15:52
 */
public class ItemCrystalSword extends ItemSword implements CrystalPropertyItem {

    private static final Random rand = new Random();
    // 1.7.10: ATTACK_DAMAGE_MODIFIER doesn't exist as field, need to create UUID
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public ItemCrystalSword() {
        super(RegistryItems.crystalToolMaterial);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial));
            list.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        ToolCrystalProperties prop = getToolProperties(stack);
        CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL * 2;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return getToolProperties(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        properties.writeToNBT(nbt);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity ei, ItemStack itemstack) {
        EntityCrystalTool newItem = new EntityCrystalTool(ei.worldObj, ei.posX, ei.posY, ei.posZ, itemstack);
        newItem.motionX = ei.motionX;
        newItem.motionY = ei.motionY;
        newItem.motionZ = ei.motionZ;
        // 1.7.10: setDefaultPickupDelay doesn't exist, set field directly
        newItem.delayBeforeCanPickup = 10;
        // 1.7.10: setThrower/setOwner don't exist in EntityItem
        // These methods were added in later versions
        return newItem;
    }

    // 1.7.10: isEnchantable doesn't exist as overrideable method
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
        damageProperties(stack, damage);
    }

    private void damageProperties(ItemStack stack, int damage) {
        ToolCrystalProperties prop = getToolProperties(stack);
        if (prop == null) {
            stack.setItemDamage(stack.getMaxDamage());
            return;
        }
        if (prop.getSize() <= 0) {
            super.setDamage(stack, 11);
            return;
        }
        if (damage < 0) {
            return;
        }
        for (int i = 0; i < damage; i++) {
            double chance = Math.pow(((double) prop.getCollectiveCapability()) / 100D, 2);
            if (chance >= rand.nextFloat()) {
                if (rand.nextInt(3) == 0) prop = prop.copyDamagedCutting();
                double purity = ((double) prop.getPurity()) / 100D;
                if (purity <= rand.nextFloat()) {
                    if (rand.nextInt(3) == 0) prop = prop.copyDamagedCutting();
                }
            }
        }
        setToolProperties(stack, prop);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    // 1.7.10: getAttributeModifiers() takes ItemStack parameter
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        // In 1.7.10, we can't easily get custom modifiers based on crystal properties
        // Return the base modifiers
        modifiers.put(
            SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
            new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 3F, 0));
        return modifiers;
    }

}
