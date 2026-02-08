/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockCollectorCrystal - Migrated from 1.12.2
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.entity.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * ItemBlock for BlockCollectorCrystal
 * <p>
 * Migrated from 1.12.2 ItemCollectorCrystal
 * <p>
 * Features:
 * - Custom highlighted entity rendering
 * - Stores constellation and trait information in NBT
 * - Rarity based on crystal type
 */
public class ItemBlockCollectorCrystal extends ItemBlock implements ItemHighlighted {

    public ItemBlockCollectorCrystal(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        // Rock crystal color
        ei.applyColor(new Color(0xDD, 0xDD, 0xFF));
        // REMOVED: setDefaultPickupDelay() - method doesn't exist in 1.7.10 EntityItem
        // 1.7.10 EntityItem doesn't have setDefaultPickupDelay method
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        if (entity instanceof EntityItem) {
            // REMOVED: setThrower and setOwner - EntityItem in 1.7.10 doesn't have these methods
            // These were 1.12.2 features for tracking thrower/owner
            // In 1.7.10, item pickup tracking is not available
        }
        return ei;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // Rock crystals have common rarity
        return EnumRarity.common;
    }

    @Override
    public Color getHighlightColor(ItemStack stack) {
        // Rock crystal color
        return new Color(0xDD, 0xDD, 0xFF);
    }

    public static void setConstellation(ItemStack stack, IWeakConstellation constellation) {
        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    public static IWeakConstellation getConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setTraitConstellation(ItemStack stack, IMinorConstellation constellation) {
        if (constellation == null) return;
        constellation.writeToNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey() + "trait");
    }

    public static IMinorConstellation getTrait(ItemStack stack) {
        return (IMinorConstellation) IConstellation
            .readFromNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey() + "trait");
    }

}
