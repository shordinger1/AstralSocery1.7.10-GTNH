/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal.base;

import java.awt.*;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.entities.EntityCrystal;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public abstract class ItemRockCrystalBase extends Item implements ItemHighlighted, CrystalPropertyItem {

    private static Random rand = new Random();

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
        if (prop == null) {
            Item i = stack.getItem();
            if (i instanceof ItemCelestialCrystal || i instanceof ItemTunedCelestialCrystal) {
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.createRandomCelestial());
            } else {
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.createRandomRock());
            }
        } else {
            if (prop.getFracturation() >= 100) {
                stack.stackSize = 0;
                entityIn.playSound(
                    null /* TODO: SoundEvents - needs 1.7.10 sound string */,
                    0.5F,
                    rand.nextFloat() * 0.2F + 0.8F);
            }
        }
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return Color.WHITE;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityCrystal crystal = new EntityCrystal(world, location.posX, location.posY, location.posZ, itemstack);
        // 1.7.10 compatibility: setDefaultPickupDelay() doesn't exist - use field directly
        crystal.delayBeforeCanPickup = 10;
        // 1.7.10 compatibility: setNoDespawn() doesn't exist - use lifespan field to prevent despawn
        crystal.lifespan = Integer.MAX_VALUE;
        crystal.motionX = location.motionX;
        crystal.motionY = location.motionY;
        crystal.motionZ = location.motionZ;
        // 1.7.10 compatibility: getThrower() and getOwner() don't exist in EntityItem - these calls removed
        // The crystal entity will work without thrower/owner tracking
        return crystal;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        addCrystalPropertyToolTip(stack, tooltip);
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    // 1.7.10 compatibility: Optional doesn't exist - use @Nullable Boolean instead
    @SideOnly(Side.CLIENT)
    @Nullable
    protected Boolean addCrystalPropertyToolTip(ItemStack stack, List<String> tooltip) {
        return CrystalProperties
            .addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, getMaxSize(stack));
    }

    public abstract ItemTunedCrystalBase getTunedItemVariant();

    public static ItemStack createMaxBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.getMaxRockProperties());
        return crystal;
    }

    public static ItemStack createMaxCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.getMaxCelestialProperties());
        return crystal;
    }

    public static ItemStack createRandomBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomRock());
        return crystal;
    }

    public static ItemStack createRandomCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomCelestial());
        return crystal;
    }

}
