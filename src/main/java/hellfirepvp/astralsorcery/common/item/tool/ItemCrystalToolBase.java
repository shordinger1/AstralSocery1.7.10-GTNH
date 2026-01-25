/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
 * Class: ItemCrystalTool
 * Created by HellFirePvP
 * Date: 18.09.2016 / 12:25
 */
public abstract class ItemCrystalToolBase extends ItemTool implements CrystalPropertyItem {

    private static final Random rand = new Random();
    private final int crystalCount;

    public ItemCrystalToolBase(int crystalCount) {
        this(crystalCount, Collections.emptySet());
    }

    public ItemCrystalToolBase(int crystalCount, Set<Block> effectiveBlocksIn) {
        // 1.7.10: ItemTool constructor takes (float damageVsEntity, ToolMaterial, effectiveBlocks)
        super(3F, RegistryItems.crystalToolMaterial, effectiveBlocksIn);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.crystalCount = crystalCount;
    }

    // 1.7.10: attackDamage and attackSpeed fields don't exist - damage is tied to toolMaterial
    public void setDamageVsEntity(float damageVsEntity) {
        // Cannot set damage directly in 1.7.10 - handled by tool material
    }

    // 1.7.10: attackSpeed doesn't exist
    public void setAttackSpeed(float attackSpeed) {
        // Cannot set attack speed in 1.7.10
    }

    public int getCrystalCount() {
        return crystalCount;
    }

    // Removed @Override - different signature in 1.7.10
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        ToolCrystalProperties prop = getToolProperties(stack);
        CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));
        // 1.7.10: ItemTool.addInformation has different signature - just skip it
    }

    // Removed @Override - not in parent class
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL * getCrystalCount();
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return getToolProperties(stack);
    }

    // Removed @Override - getDestroySpeed has different signature in 1.7.10
    public float getDestroySpeed(ItemStack stack, Block state) {
        // 1.7.10: ItemTool.getDestroySpeed doesn't exist or is private
        // Calculate base efficiency from crystal properties
        ToolCrystalProperties properties = getToolProperties(stack);
        float baseEfficiency = properties.getEfficiencyMultiplier() * 2F;
        return baseEfficiency;
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
        // 1.7.10: Use worldObj instead of world field
        EntityCrystalTool newItem = new EntityCrystalTool(ei.worldObj, ei.posX, ei.posY, ei.posZ, itemstack);
        newItem.motionX = ei.motionX;
        newItem.motionY = ei.motionY;
        newItem.motionZ = ei.motionZ;
        // 1.7.10: EntityItem doesn't have pickup delay, thrower, or owner tracking
        // These methods don't exist in 1.7.10
        if (ei instanceof EntityItem) {
            // 1.7.10: EntityItem doesn't have getThrower() or getOwner()
            // Cannot set thrower/owner in this version
        }
        return newItem;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    // Removed @Override - method doesn't exist in parent class
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
                if (rand.nextInt(8) == 0) prop = prop.copyDamagedCutting();
                double purity = ((double) prop.getPurity()) / 100D;
                if (purity <= rand.nextFloat()) {
                    if (rand.nextInt(8) == 0) prop = prop.copyDamagedCutting();
                }
            }
        }
        setToolProperties(stack, prop);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

}
